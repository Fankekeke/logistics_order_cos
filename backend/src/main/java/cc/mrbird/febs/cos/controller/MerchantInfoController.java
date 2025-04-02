package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.MerchantInfo;
import cc.mrbird.febs.cos.entity.UserInfo;
import cc.mrbird.febs.cos.service.IMerchantInfoService;
import cc.mrbird.febs.cos.service.IUserInfoService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/merchant-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MerchantInfoController {

    private final IMerchantInfoService merchantInfoService;

    private final IUserInfoService userInfoService;

    /**
     * 分页获取药店信息
     *
     * @param page         分页对象
     * @param merchantInfo 药店信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<MerchantInfo> page, MerchantInfo merchantInfo) {
        return R.ok(merchantInfoService.selectMerchantPage(page, merchantInfo));
    }

    /**
     * 根据用户ID获取药店信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/getMerchantByUser")
    public R getMerchantByUser(@RequestParam("userId") Integer userId) {
        MerchantInfo merchantInfo = merchantInfoService.getOne(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getUserId, userId));
        if (merchantInfo == null) {
            return R.ok();
        }
        return R.ok(merchantInfo);
    }

    /**
     * 药店状态审核
     *
     * @param merchantId 药店ID
     * @param status     状态
     * @return 结果
     */
    @GetMapping("audit")
    public R audit(@RequestParam("merchantId") Integer merchantId, @RequestParam("status") Integer status) {
        return R.ok(merchantInfoService.update(Wrappers.<MerchantInfo>lambdaUpdate().set(MerchantInfo::getStatus, status).eq(MerchantInfo::getId, merchantId)));
    }

    /**
     * 根据药店获取订单评价信息
     *
     * @param merchantId 药店ID
     * @return 结果
     */
    @GetMapping("/selectEvaluateByMerchant")
    public R selectEvaluateByMerchant(@RequestParam("merchantId") Integer merchantId, @RequestParam(value = "dishesId", required = false) Integer dishesId) {
        return R.ok(merchantInfoService.selectEvaluateByMerchant(merchantId, dishesId));
    }

    /**
     * 药店获取统计信息
     *
     * @param userId 药店用户ID
     * @return 结果
     */
    @GetMapping("/homeData")
    public R selectHomeDataByMerchant(@RequestParam("userId") Integer userId) {
        return R.ok(merchantInfoService.selectHomeDataByMerchant(userId));
    }

    /**
     * 管理员获取统计信息
     *
     * @return 结果
     */
    @GetMapping("/admin/homeData")
    public R selectHomeDataByAdmin() {
        return R.ok(merchantInfoService.selectHomeDataByAdmin());
    }

    /**
     * 获取ID获取药店详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        MerchantInfo merchantInfo = merchantInfoService.getById(id);
        Map<String, String> weekMap = new HashMap<String, String>() {
            {
                put("1", "周一");
                put("2", "周二");
                put("3", "周三");
                put("4", "周四");
                put("5", "周五");
                put("6", "周六");
                put("7", "周日");
            }
        };
        if (StrUtil.isNotEmpty(merchantInfo.getOperateDay())) {
            List<String> operateDayList = StrUtil.split(merchantInfo.getOperateDay(), ",");
            List<String> operateDayResult = new ArrayList<>();
            for (String s : operateDayList) {
                operateDayResult.add(weekMap.get(s));
            }
            merchantInfo.setOperateDayList(operateDayResult);
        }
        return R.ok(merchantInfo);
    }

    /**
     * 获取药店信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(merchantInfoService.list());
    }

    /**
     * 新增药店信息
     *
     * @param merchantInfo 药店信息
     * @return 结果
     */
    @PostMapping
    public R save(MerchantInfo merchantInfo) {
        Map<String, String> weekMap = new HashMap<String, String>() {
            {
                put("周一", "1");
                put("周二", "2");
                put("周三", "3");
                put("周四", "4");
                put("周五", "5");
                put("周六", "6");
                put("周日", "7");
            }
        };
        merchantInfo.setCode("MER-" + System.currentTimeMillis());
        merchantInfo.setCreateDate(DateUtil.formatDateTime(new Date()));

        if (StrUtil.isNotEmpty(merchantInfo.getOperateDay())) {
            List<String> operateDayList = StrUtil.split(merchantInfo.getOperateDay(), ",");
            List<String> operateDayResult = new ArrayList<>();
            for (String s : operateDayList) {
                operateDayResult.add(weekMap.get(s));
            }
            merchantInfo.setOperateDay(StrUtil.join(",", operateDayResult));
        }
        return R.ok(merchantInfoService.save(merchantInfo));
    }

    /**
     * 修改药店信息
     *
     * @param merchantInfo 药店信息
     * @return 结果
     */
    @PutMapping
    public R edit(MerchantInfo merchantInfo) {
        Map<String, String> weekMap = new HashMap<String, String>() {
            {
                put("周一", "1");
                put("周二", "2");
                put("周三", "3");
                put("周四", "4");
                put("周五", "5");
                put("周六", "6");
                put("周日", "7");
            }
        };
        if (StrUtil.isNotEmpty(merchantInfo.getOperateDay())) {
            List<String> operateDayList = StrUtil.split(merchantInfo.getOperateDay(), ",");
            List<String> operateDayResult = new ArrayList<>();
            for (String s : operateDayList) {
                operateDayResult.add(weekMap.get(s));
            }
            merchantInfo.setOperateDay(StrUtil.join(",", operateDayResult));
        }
        return R.ok(merchantInfoService.updateById(merchantInfo));
    }

    /**
     * 根据月份获取药品统计情况
     *
     * @param date 日期
     * @return 结果
     */
    @GetMapping("/selectStatisticsByMonth")
    public R selectStatisticsByMonth(@RequestParam("date") String date) throws FebsException {
        return R.ok(merchantInfoService.selectStatisticsByMonth(date));
    }

    /**
     * 删除药店信息
     *
     * @param ids ids
     * @return 药店信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(merchantInfoService.removeByIds(ids));
    }
}
