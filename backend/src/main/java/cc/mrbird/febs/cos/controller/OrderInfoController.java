package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.MerchantInfo;
import cc.mrbird.febs.cos.entity.OrderInfo;
import cc.mrbird.febs.cos.entity.PharmacyInventory;
import cc.mrbird.febs.cos.entity.UserInfo;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/order-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderInfoController {

    private final IOrderInfoService orderInfoService;

    private final IPharmacyInventoryService pharmacyInventoryService;

    private final TemplateEngine templateEngine;

    private final IMailService mailService;

    private final IScheduleInfoService scheduleInfoService;

    private final IUserInfoService userInfoService;

    private final IMerchantInfoService merchantInfoService;

    /**
     * 分页获取订单信息
     *
     * @param page      分页对象
     * @param orderInfo 订单信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<OrderInfo> page, OrderInfo orderInfo) {
        return R.ok(orderInfoService.selectOrderPage(page, orderInfo));
    }

    /**
     * 根据ID获取订单详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(orderInfoService.orderDetail(id));
    }

    /**
     * 查询可卖商家
     *
     * @return 结果
     */
    @GetMapping("/selectMerchantList")
    public R selectMerchantList(@RequestParam(value = "key", required = false) String key) {
        return R.ok(orderInfoService.selectMerchantList(key));
    }

    /**
     * 根据用户ID获取历史订单
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/selectMerchantMember/{userId}")
    public R selectMerchantMember(@PathVariable("userId") Integer userId) {
        return R.ok(orderInfoService.list(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getUserId, userId)));
    }

    /**
     * 获取订单评价详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/evaluate/{id}")
    public R evaluateDetail(@PathVariable("id") Integer id) {
        return R.ok(orderInfoService.evaluateDetail(id));
    }

    /**
     * 获取订单信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(orderInfoService.list());
    }

    /**
     * 新增订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @PostMapping
    public R save(OrderInfo orderInfo) throws FebsException {
        return R.ok(orderInfoService.addOrder(orderInfo));
    }

    /**
     * 新增订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @PostMapping("/saveOrder")
    public R saveOrder(OrderInfo orderInfo) throws FebsException {
        return R.ok(orderInfoService.saveOrder(orderInfo));
    }

    /**
     * 订单收货
     *
     * @param orderCode 订单编号
     * @param status    状态
     * @return 结果
     */
    @GetMapping("/audit")
    public R audit(@RequestParam("orderCode") String orderCode, @RequestParam("status") String status) throws FebsException {
        OrderInfo order = orderInfoService.getOne(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getCode, orderCode));

        UserInfo userInfo = userInfoService.getById(order.getUserId());

        // 邮箱通知
        if (StrUtil.isNotEmpty(userInfo.getMail())) {
            MerchantInfo merchantInfo = merchantInfoService.getById(order.getMerchantId());
            Context context = new Context();
            context.setVariable("today", DateUtil.formatDate(new Date()));
            context.setVariable("custom", userInfo.getName() + "，您好，在 " + merchantInfo.getName() + " 消费订单 " + order.getCode() + " 已完成，可进行评价");
            String emailContent = templateEngine.process("registerEmail", context);
            mailService.sendHtmlMail(userInfo.getMail(), DateUtil.formatDate(new Date()) + "订单完成", emailContent);
        }
        scheduleInfoService.orderCheck(order.getId());
        return R.ok(orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getStatus, status).eq(OrderInfo::getCode, orderCode)));
    }

    /**
     * 获取订单付款信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @PostMapping("/getPriceTotal")
    public R getPriceTotal(OrderInfo orderInfo) {
        return R.ok(orderInfoService.getPriceTotal(orderInfo));
    }

    /**
     * 订单退货
     *
     * @param orderCode 订单编号
     * @return 结果
     */
    @GetMapping("/orderReturn")
    public R orderReturn(String orderCode) {
        return R.ok(orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getStatus, -2).eq(OrderInfo::getCode, orderCode)));
    }

    /**
     * 订单退货审核
     *
     * @param orderCode 订单编号
     * @return 结果
     */
    @GetMapping("/returnAudit")
    public R returnAudit(String orderCode) throws FebsException {
        // 获取订单信息
        OrderInfo orderInfo = orderInfoService.getOne(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getCode, orderCode));
        if (orderInfo == null) {
            throw new FebsException("未获取到订单信息");
        }
        if (StrUtil.isNotEmpty(orderInfo.getInventoryCodes())) {
            List<String> codes = StrUtil.split(orderInfo.getInventoryCodes(), ",");
            pharmacyInventoryService.update(Wrappers.<PharmacyInventory>lambdaUpdate().set(PharmacyInventory::getShelfStatus, 1).in(PharmacyInventory::getInventoryCode, codes));
        }
        return R.ok(orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getStatus, -3).eq(OrderInfo::getCode, orderCode)));
    }

    /**
     * 修改订单状态
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @GetMapping("/editStatus")
    public R editStatus(Integer orderId) {
        return R.ok(orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getStatus, 0).eq(OrderInfo::getId, orderId)));
    }

    /**
     * 修改订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @PutMapping
    public R edit(OrderInfo orderInfo) throws FebsException {
        return R.ok(orderInfoService.editOrder(orderInfo));
    }

    /**
     * 订单支付
     *
     * @param orderCode 订单编号
     * @return 结果
     */
    @PostMapping("/orderPay")
    public R orderPay(@RequestParam("orderCode") String orderCode) {
        if (StrUtil.contains(orderCode, "CUS-")) {
            return R.ok(true);
        } else {
            return R.ok(orderInfoService.orderPay(orderCode));
        }
    }

    /**
     * 配送订单选择配送员
     *
     * @param orderCode 订单编号
     * @param staffId   员工ID
     * @return 结果
     */
    @GetMapping("/checkDealer")
    public R checkDealer(@RequestParam("orderCode") String orderCode, @RequestParam("staffId") Integer staffId) {
        return R.ok(orderInfoService.checkDealer(orderCode, staffId));
    }

    /**
     * 删除订单信息
     *
     * @param ids ids
     * @return 订单信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(orderInfoService.removeByIds(ids));
    }
}
