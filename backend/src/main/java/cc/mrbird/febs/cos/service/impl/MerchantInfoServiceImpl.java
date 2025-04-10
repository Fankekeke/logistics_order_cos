package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.dao.*;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.service.IBulletinInfoService;
import cc.mrbird.febs.cos.service.IDishesInfoService;
import cc.mrbird.febs.cos.service.IMerchantInfoService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MerchantInfoServiceImpl extends ServiceImpl<MerchantInfoMapper, MerchantInfo> implements IMerchantInfoService {

    private final OrderInfoMapper orderInfoMapper;

    private final OrderItemInfoMapper orderItemMapper;

    private final IDishesInfoService dishesInfoService;

    private final IBulletinInfoService bulletinInfoService;

    private final MerchantInfoMapper merchantInfoMapper;

    private final MerchantMemberInfoMapper merchantMemberInfoMapper;

    /**
     * 分页获取商家信息
     *
     * @param page         分页对象
     * @param merchantInfo 商家信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectMerchantPage(Page<MerchantInfo> page, MerchantInfo merchantInfo) {
        return baseMapper.selectMerchantPage(page, merchantInfo);
    }

    /**
     * 根据商家获取订单评价信息
     *
     * @param merchantId 商家ID
     * @param dishesId   商品ID
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> selectEvaluateByMerchant(Integer merchantId, Integer dishesId) {
        return baseMapper.selectEvaluateByMerchant(merchantId, dishesId);
    }

    /**
     * 商家获取统计信息
     *
     * @param userId 商家用户ID
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectHomeDataByMerchant(Integer userId) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("orderNum", 0);
                put("orderPrice", 0);
                put("staffNum", 0);
                put("memberNum", 0);
            }
        };

        // 商家信息
        MerchantInfo merchantInfo = merchantInfoMapper.selectOne(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getUserId, userId));

        List<OrderInfo> orderInfoList = orderInfoMapper.selectList(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getMerchantId, merchantInfo.getId()).ne(OrderInfo::getStatus, "0"));

        BigDecimal totalPrice = orderInfoList.stream().map(OrderInfo::getAfterOrderPrice).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("orderNum", orderInfoList.size());
        result.put("orderPrice", totalPrice);
        result.put("memberNum", merchantMemberInfoMapper.selectCount(Wrappers.<MerchantMemberInfo>lambdaQuery().eq(MerchantMemberInfo::getMerchantId, merchantInfo.getId())));

        // 本月订单数量
        List<OrderInfo> orderMonthList = orderInfoMapper.selectOrderByMonth(merchantInfo.getId());
        result.put("monthOrderNum", CollectionUtil.isEmpty(orderMonthList) ? 0 : orderMonthList.size());
        BigDecimal orderPrice = orderMonthList.stream().map(OrderInfo::getAfterOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 获取本月收益
        result.put("monthOrderTotal", orderPrice);

        // 本月支出
        BigDecimal orderExpenses1 = orderInfoMapper.queryExpensesByMonthS13X(userId);
        BigDecimal orderExpenses2 = BigDecimal.ZERO;
//        BigDecimal orderExpenses2 = orderInfoMapper.queryExpensesByMonthS12X(userId);
        result.put("monthExpensesTotal", NumberUtil.add(orderExpenses1, orderExpenses2));
        // 本月利润
        result.put("monthProfitTotal", NumberUtil.sub(orderPrice, NumberUtil.add(orderExpenses1, orderExpenses2)));

        // 本年订单数量
        List<OrderInfo> orderYearList = orderInfoMapper.selectOrderByYear(merchantInfo.getId());
        result.put("yearOrderNum", CollectionUtil.isEmpty(orderYearList) ? 0 : orderYearList.size());
        // 本年总收益
        BigDecimal orderYearPrice = orderYearList.stream().map(OrderInfo::getAfterOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("yearOrderTotal", orderYearPrice);

        // 近十天销售订单统计
        result.put("orderNumDayList", orderInfoMapper.selectOrderNumWithinDays(merchantInfo.getId()));
        // 近十天销售金额统计
        result.put("priceDayList", orderInfoMapper.selectOrderPriceWithinDays(merchantInfo.getId()));
        // 销售商品统计
        result.put("orderDrugType", orderInfoMapper.selectOrderDishesType(merchantInfo.getId()));
        // 公告信息
        result.put("bulletinInfoList", bulletinInfoService.list(Wrappers.<BulletinInfo>lambdaQuery().eq(BulletinInfo::getRackUp, 1)));

        return result;
    }

    /**
     * 管理员获取统计信息
     *
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectHomeDataByAdmin() {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("orderNum", 0);
                put("orderPrice", 0);
                put("staffNum", 0);
                put("merchantNum", 0);
            }
        };

        List<OrderInfo> orderInfoList = orderInfoMapper.selectList(Wrappers.<OrderInfo>lambdaQuery().ne(OrderInfo::getStatus, "0"));

        BigDecimal totalPrice = orderInfoList.stream().map(OrderInfo::getAfterOrderPrice).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("orderNum", orderInfoList.size());
        result.put("orderPrice", totalPrice);
        result.put("merchantNum", merchantInfoMapper.selectCount(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getStatus, "1")));

        // 本月订单数量
        List<OrderInfo> orderMonthList = orderInfoMapper.selectOrderByMonth(null);
        result.put("monthOrderNum", CollectionUtil.isEmpty(orderMonthList) ? 0 : orderMonthList.size());
        BigDecimal orderPrice = orderMonthList.stream().map(OrderInfo::getAfterOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 获取本月收益
        result.put("monthOrderTotal", orderPrice);

        // 本年订单数量
        List<OrderInfo> orderYearList = orderInfoMapper.selectOrderByYear(null);
        result.put("yearOrderNum", CollectionUtil.isEmpty(orderYearList) ? 0 : orderYearList.size());
        // 本年总收益
        BigDecimal orderYearPrice = orderYearList.stream().map(OrderInfo::getAfterOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("yearOrderTotal", orderYearPrice);

        // 近十天销售订单统计
        result.put("orderNumDayList", orderInfoMapper.selectOrderNumWithinDays(null));
        // 近十天销售金额统计
        result.put("priceDayList", orderInfoMapper.selectOrderPriceWithinDays(null));
        // 销售商品统计
        result.put("orderDrugType", orderInfoMapper.selectOrderDishesType(null));
        // 公告信息
        result.put("bulletinInfoList", bulletinInfoService.list(Wrappers.<BulletinInfo>lambdaQuery().eq(BulletinInfo::getRackUp, 1)));

        return result;
    }

    /**
     * 根据月份获取商品统计情况
     *
     * @param date 日期
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectStatisticsByMonth(String date) throws FebsException {
        if (StrUtil.isEmpty(date)) {
            throw new FebsException("参数不能为空");
        }

        int year = DateUtil.year(DateUtil.parseDate(date));
        int month = DateUtil.month(DateUtil.parseDate(date)) + 1;

        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("num", Collections.emptyList());
                put("price", Collections.emptyList());
            }
        };

        // 获取订单详情
        List<OrderInfo> orderList = orderInfoMapper.selectOrderByCheckMonth(year, month);
        if (CollectionUtil.isEmpty(orderList)) {
            return result;
        }

        List<Map<String, Object>> numMap = new ArrayList<>();
        List<Map<String, Object>> priceMap = new ArrayList<>();

        List<Integer> orderIds = orderList.stream().map(OrderInfo::getId).collect(Collectors.toList());
        List<OrderItemInfo> detailList = orderItemMapper.selectList(Wrappers.<OrderItemInfo>lambdaQuery().in(OrderItemInfo::getOrderId, orderIds));
        // 按商品ID分组
        Map<Integer, List<OrderItemInfo>> drugDetailMap = detailList.stream().collect(Collectors.groupingBy(OrderItemInfo::getDishesId));

        // 商品信息
        List<DishesInfo> drugInfoList = (List<DishesInfo>) dishesInfoService.listByIds(drugDetailMap.keySet());
        Map<Integer, String> drugMap = drugInfoList.stream().collect(Collectors.toMap(DishesInfo::getId, DishesInfo::getName));

        drugDetailMap.forEach((key, value) -> {
            String drugName = drugMap.get(key);
            Map<String, Object> numItem = new HashMap<String, Object>() {
                {
                    put("name", drugName);
                }
            };
            Map<String, Object> priceItem = new HashMap<String, Object>() {
                {
                    put("name", drugName);
                }
            };
            // 本月商品销售数量统计
            int num = value.stream().map(OrderItemInfo::getAmount).reduce(0, Integer::sum);
            numItem.put("value", num);

            // 本月商品销售金额统计
            BigDecimal price = value.stream().map(OrderItemInfo::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            priceItem.put("value", price);
            numMap.add(numItem);
            priceMap.add(priceItem);
        });

        result.put("num", numMap);
        result.put("price", priceMap);
        return result;
    }
}
