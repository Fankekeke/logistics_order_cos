package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.OrderTemplateInfo;
import cc.mrbird.febs.cos.service.IOrderTemplateInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/order-template-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderTemplateInfoController {

    private final IOrderTemplateInfoService orderTemplateInfoService;

    /**
     * 分页获取小票模板信息
     *
     * @param page              分页对象
     * @param orderTemplateInfo 小票模板信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<OrderTemplateInfo> page, OrderTemplateInfo orderTemplateInfo) {
        return R.ok(orderTemplateInfoService.queryOrderTemplatePage(page, orderTemplateInfo));
    }

    /**
     * 获取ID获取小票模板详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(orderTemplateInfoService.getById(id));
    }

    /**
     * 获取小票模板信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(orderTemplateInfoService.list());
    }

    /**
     * 获取默认模板
     *
     * @return 结果
     */
    @GetMapping("/queryDefaultTemplate")
    public R queryDefaultTemplate() {
        return R.ok(orderTemplateInfoService.getOne(Wrappers.<OrderTemplateInfo>lambdaQuery().eq(OrderTemplateInfo::getDefaultFlag, 1)));
    }

    /**
     * 新增小票模板信息
     *
     * @param orderTemplateInfo 小票模板信息
     * @return 结果
     */
    @PostMapping
    public R save(OrderTemplateInfo orderTemplateInfo) {
        orderTemplateInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        if ("1".equals(orderTemplateInfo.getDefaultFlag())) {
            orderTemplateInfoService.update(Wrappers.<OrderTemplateInfo>lambdaUpdate().set(OrderTemplateInfo::getDefaultFlag, 0).eq(OrderTemplateInfo::getDefaultFlag, 1));
        }
        return R.ok(orderTemplateInfoService.save(orderTemplateInfo));
    }

    /**
     * 修改小票模板信息
     *
     * @param orderTemplateInfo 小票模板信息
     * @return 结果
     */
    @PutMapping
    public R edit(OrderTemplateInfo orderTemplateInfo) {
        if ("1".equals(orderTemplateInfo.getDefaultFlag())) {
            orderTemplateInfoService.update(Wrappers.<OrderTemplateInfo>lambdaUpdate().set(OrderTemplateInfo::getDefaultFlag, 0).eq(OrderTemplateInfo::getDefaultFlag, 1));
        }
        return R.ok(orderTemplateInfoService.updateById(orderTemplateInfo));
    }

    /**
     * 删除小票模板信息
     *
     * @param ids ids
     * @return 小票模板信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(orderTemplateInfoService.removeByIds(ids));
    }
}
