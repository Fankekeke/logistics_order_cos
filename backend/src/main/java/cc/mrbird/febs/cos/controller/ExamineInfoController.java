package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ExamineInfo;
import cc.mrbird.febs.cos.entity.PharmacyInventory;
import cc.mrbird.febs.cos.entity.PurchaseInfo;
import cc.mrbird.febs.cos.service.IExamineInfoService;
import cc.mrbird.febs.cos.service.IPharmacyInventoryService;
import cc.mrbird.febs.cos.service.IPurchaseInfoService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/examine-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExamineInfoController {

    private final IExamineInfoService examineInfoService;

    private final IPurchaseInfoService purchaseInfoService;

    private final IPharmacyInventoryService inventoryService;

    /**
     * 分页获取问题检查信息
     *
     * @param page         分页对象
     * @param examineInfo 问题检查信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<ExamineInfo> page, ExamineInfo examineInfo) {
        return R.ok(examineInfoService.queryExaminePage(page, examineInfo));
    }

    /**
     * 获取ID获取问题检查详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(examineInfoService.getById(id));
    }

    /**
     * 获取问题检查信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(examineInfoService.list());
    }

    /**
     * 新增问题检查信息
     *
     * @param examineInfo 问题检查信息
     * @return 结果
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R save(ExamineInfo examineInfo) throws FebsException {
        // 获取采购单信息
        PurchaseInfo purchaseInfo = purchaseInfoService.getOne(Wrappers.<PurchaseInfo>lambdaQuery().eq(PurchaseInfo::getCode, examineInfo.getPurchaseCode()));
        if (purchaseInfo == null) {
            throw new FebsException("采购单信息不存在");
        }

        // 获取采购库存
        List<PharmacyInventory> inventoryList = inventoryService.list(Wrappers.<PharmacyInventory>lambdaQuery().eq(PharmacyInventory::getDrugId, examineInfo.getDrugId())
                .eq(PharmacyInventory::getPurchaseCode, purchaseInfo.getCode()));
        if (CollectionUtil.isEmpty(inventoryList)) {
            throw new FebsException("采购单库存不存在");
        }

        List<PharmacyInventory> updateList = new ArrayList<>();
        for (PharmacyInventory inventory : inventoryList) {
            inventory.setShelfStatus(2);
            updateList.add(inventory);
        }
        inventoryService.updateBatchById(updateList);

        List<String> stockCodes = updateList.stream().map(PharmacyInventory::getInventoryCode).collect(Collectors.toList());
        examineInfo.setStockCodes(StrUtil.join(",", stockCodes));
        examineInfo.setMerchantId(purchaseInfo.getPharmacyId());
        examineInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        examineInfo.setAmount(inventoryList.size());
        return R.ok(examineInfoService.save(examineInfo));
    }

    /**
     * 修改问题检查信息
     *
     * @param examineInfo 问题检查信息
     * @return 结果
     */
    @PutMapping
    public R edit(ExamineInfo examineInfo) {
        return R.ok(examineInfoService.updateById(examineInfo));
    }

    /**
     * 删除问题检查信息
     *
     * @param ids ids
     * @return 问题检查信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(examineInfoService.removeByIds(ids));
    }
}
