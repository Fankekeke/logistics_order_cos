package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.dao.PurchaseInfoMapper;
import cc.mrbird.febs.cos.entity.DishesInfo;
import cc.mrbird.febs.cos.entity.LogisticsInfo;
import cc.mrbird.febs.cos.entity.PharmacyInventory;
import cc.mrbird.febs.cos.entity.PurchaseInfo;
import cc.mrbird.febs.cos.service.IDishesInfoService;
import cc.mrbird.febs.cos.service.IPharmacyInventoryService;
import cc.mrbird.febs.cos.service.IPurchaseInfoService;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PurchaseInfoServiceImpl extends ServiceImpl<PurchaseInfoMapper, PurchaseInfo> implements IPurchaseInfoService {

    private final IDishesInfoService dishesInfoService;

    private final IPharmacyInventoryService inventoryService;

    /**
     * 分页获取商品采购信息
     *
     * @param page 分页对象
     * @param purchaseInfo 商品采购信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectPurchasePage(Page<PurchaseInfo> page, PurchaseInfo purchaseInfo) {
        return baseMapper.selectPurchasePage(page, purchaseInfo);
    }

    /**
     * 收货
     *
     * @param id 采购ID
     * @return 结果
     */
    @Override
    public boolean receipt(Integer id) throws Exception {
        // 获取采购信息
        PurchaseInfo purchase = this.getById(id);
        purchase.setStatus(2);
        inventoryService.batchPutInventory(purchase.getPharmacyId(), purchase.getPurchaseDrug(), purchase.getCode());
        return this.updateById(purchase);
    }

    /**
     * 新增商品采购信息
     *
     * @param purchaseInfo 商品采购信息
     * @return 结果
     */
    @Override
    public boolean purchaseAdd(PurchaseInfo purchaseInfo) {
        List<PharmacyInventory> inventoryList = JSONUtil.toList(purchaseInfo.getPurchaseDrug(), PharmacyInventory.class);
        BigDecimal totalPrice = BigDecimal.ZERO;
        int amount = 0;

        // 计算采购数量与总价格
        for (PharmacyInventory item : inventoryList) {
            amount += item.getReserve();
            BigDecimal itemPrice = NumberUtil.mul(item.getReserve(), item.getReceiveUnitPrice());
            totalPrice = totalPrice.add(itemPrice);
        }

        purchaseInfo.setAmount(amount);
        purchaseInfo.setTotalPrice(totalPrice);

        return this.save(purchaseInfo);
    }

    /**
     * 采购单详情-商品物流
     *
     * @param id 采购ID
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> detailPurchase(String id) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("drug", null);
                put("logistics", Collections.emptyList());
            }
        };

        // 采购单信息
        PurchaseInfo purchase = this.getById(id);

        List<PharmacyInventory> inventoryList = JSONUtil.toList(purchase.getPurchaseDrug(), PharmacyInventory.class);
        List<Integer> drugIds = inventoryList.stream().map(PharmacyInventory::getDrugId).collect(Collectors.toList());
        List<DishesInfo> drugInfoList = (List<DishesInfo>) dishesInfoService.listByIds(drugIds);

        Map<Integer, DishesInfo> drugMap = drugInfoList.stream().collect(Collectors.toMap(DishesInfo::getId, e -> e));
        for (PharmacyInventory pharmacy : inventoryList) {
            DishesInfo drugInfo = drugMap.get(pharmacy.getDrugId());
            if (null == drugInfo) {
                continue;
            }
            pharmacy.setDrugName(drugInfo.getName());
            pharmacy.setRawMaterial(drugInfo.getRawMaterial());
            pharmacy.setImages(drugInfo.getImages());
        }

        result.put("drug", inventoryList);

        if (StrUtil.isNotEmpty(purchase.getLogistics())) {
            List<LogisticsInfo> logisticsList = JSONUtil.toList(purchase.getLogistics(), LogisticsInfo.class);
            result.put("logistics", logisticsList);
        }
        return result;
    }
}
