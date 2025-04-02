package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.dao.StockAlertInfoMapper;
import cc.mrbird.febs.cos.entity.DishesInfo;
import cc.mrbird.febs.cos.entity.MerchantInfo;
import cc.mrbird.febs.cos.entity.PharmacyInventory;
import cc.mrbird.febs.cos.entity.StockAlertInfo;
import cc.mrbird.febs.cos.service.IDishesInfoService;
import cc.mrbird.febs.cos.service.IMerchantInfoService;
import cc.mrbird.febs.cos.service.IPharmacyInventoryService;
import cc.mrbird.febs.cos.service.IStockAlertInfoService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockAlertInfoServiceImpl extends ServiceImpl<StockAlertInfoMapper, StockAlertInfo> implements IStockAlertInfoService {

    private final IMerchantInfoService merchantInfoService;

    private final IPharmacyInventoryService pharmacyInventoryService;

    private final IDishesInfoService dishesInfoService;

    /**
     * 分页获取库房预警信息
     *
     * @param page           分页对象
     * @param stockAlertInfo 库房预警信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectStockAlertPage(Page<StockAlertInfo> page, StockAlertInfo stockAlertInfo) {
        return baseMapper.selectStockAlertPage(page, stockAlertInfo);
    }

    /**
     * 库存预警校验
     */
    @Override
    public void stockAlertCheck() {
        List<MerchantInfo> pharmacyInfoList = merchantInfoService.list(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getStatus, 1));
        if (CollectionUtil.isEmpty(pharmacyInfoList)) {
            return;
        }
        List<Integer> pharmacyIds = pharmacyInfoList.stream().map(MerchantInfo::getId).collect(Collectors.toList());

        // 获取药店库存
        List<PharmacyInventory> inventoryList = pharmacyInventoryService.list(Wrappers.<PharmacyInventory>lambdaQuery().in(PharmacyInventory::getPharmacyId, pharmacyIds).eq(PharmacyInventory::getShelfStatus, 1));
        if (CollectionUtil.isEmpty(inventoryList)) {
            return;
        }
        // 按药店分组
        Map<Integer, List<PharmacyInventory>> inventoryMap = inventoryList.stream().collect(Collectors.groupingBy(PharmacyInventory::getPharmacyId));

        List<Integer> drugIds = inventoryList.stream().map(PharmacyInventory::getDrugId).distinct().collect(Collectors.toList());
        // 药品信息
        List<DishesInfo> drugInfoList = (List<DishesInfo>) dishesInfoService.listByIds(drugIds);
        Map<Integer, String> drugMap = drugInfoList.stream().collect(Collectors.toMap(DishesInfo::getId, DishesInfo::getName));

        // 待更新数据
        List<StockAlertInfo> stockAlertInfoList = new ArrayList<>();

        for (Integer pharmacyId : pharmacyIds) {
            // 获取该药店库存信息
            List<PharmacyInventory> currentPharmacyInventoryList = inventoryMap.get(pharmacyId);
            if (CollectionUtil.isEmpty(currentPharmacyInventoryList)) {
                continue;
            }

            // 库存信息按药品分类
            Map<Integer, List<PharmacyInventory>> inventoryMap1 = currentPharmacyInventoryList.stream().collect(Collectors.groupingBy(PharmacyInventory::getDrugId));
            inventoryMap1.forEach((key, value) -> {
                if (value.size() <= 15) {
                    StockAlertInfo stockAlert = new StockAlertInfo();
                    stockAlert.setDurgId(key);
                    stockAlert.setShopId(pharmacyId);
                    stockAlert.setStatus(0);
                    stockAlert.setCreateDate(DateUtil.formatDateTime(new Date()));
                    stockAlert.setRemark("药品【" + drugMap.get(key) + "】库存数量为 " + value.size() + ", 请尽快补货");
                    stockAlertInfoList.add(stockAlert);
                }
            });
        }

        if (CollectionUtil.isNotEmpty(stockAlertInfoList)) {
            this.saveBatch(stockAlertInfoList);
        }
    }
}
