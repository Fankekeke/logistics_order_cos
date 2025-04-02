package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.WarrantyInfo;
import cc.mrbird.febs.cos.service.IWarrantyInfoService;
import cn.hutool.core.date.DateUtil;
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
@RequestMapping("/cos/warranty-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarrantyInfoController {

    private final IWarrantyInfoService warrantyInfoService;

    /**
     * 分页获取保质期预警信息
     *
     * @param page         分页对象
     * @param warrantyInfo 保质期预警信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<WarrantyInfo> page, WarrantyInfo warrantyInfo) {
        return R.ok(warrantyInfoService.queryWarrantyPage(page, warrantyInfo));
    }

    /**
     * 获取ID获取保质期预警详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(warrantyInfoService.getById(id));
    }

    /**
     * 获取保质期预警信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(warrantyInfoService.list());
    }

    /**
     * 新增保质期预警信息
     *
     * @param warrantyInfo 保质期预警信息
     * @return 结果
     */
    @PostMapping
    public R save(WarrantyInfo warrantyInfo) {
        warrantyInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(warrantyInfoService.save(warrantyInfo));
    }

    /**
     * 修改保质期预警信息
     *
     * @param warrantyInfo 保质期预警信息
     * @return 结果
     */
    @PutMapping
    public R edit(WarrantyInfo warrantyInfo) {
        return R.ok(warrantyInfoService.updateById(warrantyInfo));
    }

    /**
     * 删除保质期预警信息
     *
     * @param ids ids
     * @return 保质期预警信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(warrantyInfoService.removeByIds(ids));
    }
}
