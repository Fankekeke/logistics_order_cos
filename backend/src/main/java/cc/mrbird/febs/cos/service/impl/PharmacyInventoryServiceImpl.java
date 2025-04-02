package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.dao.DishesInfoMapper;
import cc.mrbird.febs.cos.dao.PharmacyInventoryMapper;
import cc.mrbird.febs.cos.dao.PurchaseInfoMapper;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.service.IInventoryStatisticsService;
import cc.mrbird.febs.cos.service.IOrderInfoService;
import cc.mrbird.febs.cos.service.IOrderItemInfoService;
import cc.mrbird.febs.cos.service.IPharmacyInventoryService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.domain.OrderDetail;
import com.alipay.api.domain.OrderItem;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PharmacyInventoryServiceImpl extends ServiceImpl<PharmacyInventoryMapper, PharmacyInventory> implements IPharmacyInventoryService {

    private final IInventoryStatisticsService inventoryStatisticsService;

    private final PurchaseInfoMapper purchaseInfoMapper;
    private final DishesInfoMapper dishesInfoMapper;

    private final IOrderItemInfoService orderDetailService;

    /**
     * 分页获取药店库存信息
     *
     * @param page              分页对象
     * @param pharmacyInventory 药店库存信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectPharmacyInventoryPage(Page<PharmacyInventory> page, PharmacyInventory pharmacyInventory) {
        return baseMapper.selectPharmacyInventoryPage(page, pharmacyInventory);
    }

    /**
     * 订单出货后设置库存
     *
     * @param orderInfo 订单编号
     * @return 结果
     */
    @Override
    public boolean setPharmacyInventory(OrderInfo orderInfo) {
        // 获取订单详情信息
        List<OrderItemInfo> orderDetailList = orderDetailService.list(Wrappers.<OrderItemInfo>lambdaQuery().eq(OrderItemInfo::getOrderId, orderInfo.getId()));
        if (CollectionUtil.isEmpty(orderDetailList)) {
            return false;
        }

        // orderDetailList根据DishesId分组
        Map<Integer, List<OrderItemInfo>> orderDetailMap = orderDetailList.stream().collect(Collectors.groupingBy(OrderItemInfo::getDishesId));

        // 获取药店库存
        List<PharmacyInventory> inventoryList = baseMapper.selectList(Wrappers.<PharmacyInventory>lambdaQuery()
                .eq(PharmacyInventory::getPharmacyId, orderInfo.getMerchantId())
                .eq(PharmacyInventory::getShelfStatus, 1)
                .in(PharmacyInventory::getDrugId, orderDetailMap.keySet()));
        // 带更新的库存信息
        List<PharmacyInventory> updateInventoryList = new ArrayList<>();
        // inventoryList按药品ID分组
        Map<Integer, List<PharmacyInventory>> inventoryMap = inventoryList.stream().collect(Collectors.groupingBy(PharmacyInventory::getDrugId));

        orderDetailMap.forEach((k, v) -> {
            OrderItemInfo orderDetail = v.get(0);
            List<PharmacyInventory> currentPharmacyInventoryList = inventoryMap.get(k);
            if (CollectionUtil.isNotEmpty(currentPharmacyInventoryList)) {
                currentPharmacyInventoryList = currentPharmacyInventoryList.stream().sorted(Comparator.comparing(PharmacyInventory::getEndDate).reversed()).collect(Collectors.toList());
                for (Integer i = 0; i < orderDetail.getAmount(); i++) {
                    PharmacyInventory currentPharmacyInventory = currentPharmacyInventoryList.get(i);
                    currentPharmacyInventory.setShelfStatus(3);
                    updateInventoryList.add(currentPharmacyInventory);
                }
            }
        });
        if (CollectionUtil.isNotEmpty(updateInventoryList)) {
            this.updateBatchById(updateInventoryList);

            List<String> stockCodes = updateInventoryList.stream().map(PharmacyInventory::getInventoryCode).collect(Collectors.toList());
            orderInfo.setInventoryCodes(StrUtil.join(",", stockCodes));
        }
        return true;
    }

    /**
     * 根据药房ID获取库存信息
     *
     * @param pharmacyId 药房ID
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectInventoryByPharmacyTRS1R(Integer pharmacyId) {
        // 根据商家获取上架的药品
        List<DishesInfo> dishesInfoList = dishesInfoMapper.selectList(Wrappers.<DishesInfo>lambdaQuery().eq(DishesInfo::getMerchantId, pharmacyId).eq(DishesInfo::getStatus, 1));
        if (CollectionUtil.isEmpty(dishesInfoList)) {
            return Collections.emptyList();
        }

        List<Integer> dishesIdList = dishesInfoList.stream().map(DishesInfo::getId).collect(Collectors.toList());
        return baseMapper.selectInventoryByPharmacyTRS1R(pharmacyId, dishesIdList);
    }

    /**
     * 根据采购单号查询正常药品信息
     *
     * @param purchaseCode 采购单号
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryDrugByPurchaseCode(String purchaseCode) {
        // 返回数据
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        // 获取采购单信息
        PurchaseInfo purchaseInfo = purchaseInfoMapper.selectOne(Wrappers.<PurchaseInfo>lambdaQuery().eq(PurchaseInfo::getCode, purchaseCode));
        if (purchaseInfo == null) {
            return result;
        }
        // 获取药品信息
        List<PharmacyInventory> pharmacyInventoryList = baseMapper.selectList(Wrappers.<PharmacyInventory>lambdaQuery().eq(PharmacyInventory::getPurchaseCode, purchaseCode).eq(PharmacyInventory::getShelfStatus, 1));
        if (CollectionUtil.isEmpty(pharmacyInventoryList)) {
            return result;
        }

        // 根据ID转MAP
        Map<Integer, List<PharmacyInventory>> inventoryMap = pharmacyInventoryList.stream().collect(Collectors.groupingBy(PharmacyInventory::getDrugId));

        // 获取药品信息
        List<DishesInfo> dishesInfoList = dishesInfoMapper.selectList(Wrappers.<DishesInfo>lambdaQuery());
        // 根据ID转MAP
        Map<Integer, DishesInfo> dishesMap = dishesInfoList.stream().collect(Collectors.toMap(DishesInfo::getId, e -> e));

        inventoryMap.forEach((k, v) -> {
            LinkedHashMap<String, Object> resultItem = new LinkedHashMap<>();
            DishesInfo dishesInfo = dishesMap.get(k);
            if (dishesInfo != null) {
                resultItem.put("drugId", k);
                resultItem.put("drugName", dishesInfo.getName());
                resultItem.put("rawMaterial", dishesInfo.getRawMaterial());
                resultItem.put("images", dishesInfo.getImages());
                resultItem.put("receiveUnitPrice", dishesInfo.getReceiveUnitPrice());
                resultItem.put("reserve", v.size());
                resultItem.put("endDate", v.get(0).getEndDate());
                result.add(resultItem);
            }
        });
        return result;
    }

    /**
     * 获取药品信息
     *
     * @param key key
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectPharmacyDrugList(String key) {
        return baseMapper.selectPharmacyDrugList(key);
    }

    /**
     * 批量设置库房库存
     *
     * @param pharmacyId         参数
     * @param pharmacyInventorys 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public boolean batchPutInventory(Integer pharmacyId, String pharmacyInventorys, String purchaseCode) throws Exception {
        List<PharmacyInventory> inventoryList = JSONUtil.toList(pharmacyInventorys, PharmacyInventory.class);
        if (pharmacyId == null || CollectionUtil.isEmpty(inventoryList)) {
            throw new FebsException("所属药店和药品信息不能为空！");
        }
        List<Integer> drugIds = inventoryList.stream().map(PharmacyInventory::getDrugId).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(drugIds)) {
            return false;
        }
        // 根据药品编号查询库存
        List<PharmacyInventory> pharmacyInventoryList = this.list(Wrappers.<PharmacyInventory>lambdaQuery().eq(PharmacyInventory::getPharmacyId, pharmacyId).in(PharmacyInventory::getDrugId, drugIds));
        // 转MAP
//        Map<Integer, PharmacyInventory> inventoryMap = pharmacyInventoryList.stream().collect(Collectors.toMap(PharmacyInventory::getDrugId, e -> e));

        List<PharmacyInventory> batchData = new ArrayList<>();
        List<InventoryStatistics> statisticsList = new ArrayList<>();
        for (PharmacyInventory pharmacyInventoryVo : inventoryList) {
            // 添加新库存
            for (int i = 0; i < pharmacyInventoryVo.getReserve(); i++) {
                PharmacyInventory item = new PharmacyInventory();
                item.setPharmacyId(pharmacyId);
                item.setShelfStatus(1);
                item.setDrugId(pharmacyInventoryVo.getDrugId());
                item.setEndDate(pharmacyInventoryVo.getEndDate());
                item.setPurchaseCode(pharmacyInventoryVo.getPurchaseCode());
                item.setReserve(1);
                item.setPurchaseCode(purchaseCode);
                item.setInventoryCode(UUID.randomUUID().toString());
                batchData.add(item);

                // 记录库存编号
                InventoryStatistics inventoryStatistics = new InventoryStatistics();
                inventoryStatistics.setDrugId(pharmacyInventoryVo.getDrugId());
                inventoryStatistics.setPharmacyId(pharmacyId);
                inventoryStatistics.setQuantity(1);
                inventoryStatistics.setCreateDate(DateUtil.formatDateTime(new Date()));
                inventoryStatistics.setStorageType(2);
                inventoryStatistics.setInventoryCode(item.getInventoryCode());
                statisticsList.add(inventoryStatistics);
            }

        }
        inventoryStatisticsService.saveBatch(statisticsList);
        return this.saveOrUpdateBatch(batchData);
    }

    /**
     * 根据药房ID获取库存信息
     *
     * @param pharmacyId 药房ID
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectInventoryByPharmacy(Integer pharmacyId) {
        return baseMapper.selectInventoryByPharmacy(pharmacyId);
    }

    /**
     * 设置库存
     *
     * @param pharmacyInventory 药店库存信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean putInventory(PharmacyInventory pharmacyInventory) {
        // 判断库存中是否存在
        PharmacyInventory inventory = this.getOne(Wrappers.<PharmacyInventory>lambdaQuery().eq(PharmacyInventory::getPharmacyId, pharmacyInventory.getPharmacyId()).eq(PharmacyInventory::getDrugId, pharmacyInventory.getDrugId()));
        // 添加入库记录
        InventoryStatistics inventoryStatistics = new InventoryStatistics();
        inventoryStatistics.setPharmacyId(pharmacyInventory.getPharmacyId());
        inventoryStatistics.setDrugId(pharmacyInventory.getDrugId());
        inventoryStatistics.setQuantity(pharmacyInventory.getReserve());
        inventoryStatistics.setStorageType(1);
        inventoryStatistics.setCreateDate(DateUtil.formatDateTime(new Date()));
        inventoryStatisticsService.save(inventoryStatistics);
        if (inventory == null) {
            pharmacyInventory.setShelfStatus(1);
            return this.save(pharmacyInventory);
        }
        inventory.setReserve(inventory.getReserve() + pharmacyInventory.getReserve());
        return this.save(inventory);
    }

    /**
     * 设置库存
     *
     * @param pharmacyInventory 药店库存信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean outInventory(PharmacyInventory pharmacyInventory) {
        PharmacyInventory inventory = this.getOne(Wrappers.<PharmacyInventory>lambdaQuery().eq(PharmacyInventory::getPharmacyId, pharmacyInventory.getPharmacyId()).eq(PharmacyInventory::getDrugId, pharmacyInventory.getDrugId()));
        // 添加入库记录
        InventoryStatistics inventoryStatistics = new InventoryStatistics();
        inventoryStatistics.setPharmacyId(pharmacyInventory.getPharmacyId());
        inventoryStatistics.setDrugId(pharmacyInventory.getDrugId());
        inventoryStatistics.setQuantity(pharmacyInventory.getReserve());
        inventoryStatistics.setStorageType(2);
        inventoryStatistics.setCreateDate(DateUtil.formatDateTime(new Date()));
        inventoryStatisticsService.save(inventoryStatistics);
        inventory.setReserve(inventory.getReserve() - pharmacyInventory.getReserve());
        return this.updateById(inventory);
    }


}
