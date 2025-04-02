package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.TypeInfo;
import cc.mrbird.febs.cos.service.ITypeInfoService;
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
@RequestMapping("/cos/firniture-type-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TypeInfoController {

    private final ITypeInfoService firnitureTypeInfoService;

    /**
     * 分页获取药品类型信息
     *
     * @param page              分页对象
     * @param typeInfo 药品类型信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<TypeInfo> page, TypeInfo typeInfo) {
        return R.ok(firnitureTypeInfoService.selectFirnitureTypePage(page, typeInfo));
    }

    /**
     * 获取ID获取药品类型详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(firnitureTypeInfoService.getById(id));
    }

    /**
     * 获取药品类型信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(firnitureTypeInfoService.list());
    }

    /**
     * 新增药品类型信息
     *
     * @param typeInfo 药品类型信息
     * @return 结果
     */
    @PostMapping
    public R save(TypeInfo typeInfo) {
        typeInfo.setCode("FT-" + System.currentTimeMillis());
        typeInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(firnitureTypeInfoService.save(typeInfo));
    }

    /**
     * 修改药品类型信息
     *
     * @param typeInfo 药品类型信息
     * @return 结果
     */
    @PutMapping
    public R edit(TypeInfo typeInfo) {
        return R.ok(firnitureTypeInfoService.updateById(typeInfo));
    }

    /**
     * 删除药品类型信息
     *
     * @param ids ids
     * @return 药品类型信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(firnitureTypeInfoService.removeByIds(ids));
    }
}
