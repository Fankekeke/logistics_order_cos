package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.LocationUtils;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.dao.ScheduleInfoMapper;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduleInfoServiceImpl extends ServiceImpl<ScheduleInfoMapper, ScheduleInfo> implements IScheduleInfoService {

    private final IVehicleInfoService vehicleInfoService;

    private final IMerchantInfoService merchantInfoService;

    private final IAddressInfoService addressInfoService;

    private final IOrderInfoService orderInfoService;

    private final IUserInfoService userInfoService;

    /**
     * 订单绑定未工作车辆
     *
     * @param vehicleId 车辆ID
     * @param orderId   订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean orderBindSchedule(Integer vehicleId, Integer orderId) throws FebsException {
        // 获取车辆信息
        VehicleInfo vehicleInfo = vehicleInfoService.getById(vehicleId);
        if (vehicleInfo == null) {
            throw new FebsException("车辆信息不存在！");
        }

        // 获取商家地址
        MerchantInfo merchantInfo = merchantInfoService.getById(vehicleInfo.getMerchantId());
        if (merchantInfo == null) {
            throw new FebsException("商家信息不存在！");
        }

        // 获取订单信息
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        if (orderInfo == null) {
            throw new FebsException("订单信息不存在！");
        }

        // 收货地址
        AddressInfo addressInfo = addressInfoService.getById(orderInfo.getAddressId());
        if (addressInfo == null) {
            throw new FebsException("收货地址信息不存在！");
        }

        String scheduleCode = "";

        // 绑定订单
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setVehicleId(vehicleId);
        scheduleInfo.setOrderId(orderId);
        scheduleInfo.setPreviousLongitude(merchantInfo.getLongitude());
        scheduleInfo.setPreviousLatitude(merchantInfo.getLatitude());
        scheduleInfo.setCurrentLongitude(addressInfo.getLongitude());
        scheduleInfo.setCurrentLatitude(addressInfo.getLatitude());
        scheduleInfo.setNextLongitude(merchantInfo.getLongitude());
        scheduleInfo.setNextLatitude(merchantInfo.getLatitude());
        scheduleInfo.setIndexNo(1);
        scheduleInfo.setStatus("0");
        double distance = LocationUtils.getDistance(merchantInfo.getLongitude().doubleValue(), merchantInfo.getLatitude().doubleValue(), addressInfo.getLongitude().doubleValue(), addressInfo.getLatitude().doubleValue());
        scheduleInfo.setDistance(BigDecimal.valueOf(distance));

        // 判断车辆是否已绑定工单
        if (StrUtil.isEmpty(vehicleInfo.getScheduleCode())) {
            scheduleCode = "SCH-" + System.currentTimeMillis();
            // 更新车辆车次
            vehicleInfo.setScheduleCode(scheduleCode);
            vehicleInfoService.updateById(vehicleInfo);

            scheduleInfo.setScheduleCode(scheduleCode);
            return this.save(scheduleInfo);
        } else {
            scheduleCode = vehicleInfo.getScheduleCode();
            scheduleInfo.setScheduleCode(scheduleCode);
        }

        // 获取此车次信息
        List<ScheduleInfo> scheduleInfoList = this.list(Wrappers.<ScheduleInfo>lambdaQuery().eq(ScheduleInfo::getScheduleCode, scheduleCode));
        scheduleInfoList.add(scheduleInfo);

        // 按照距离排序，距离最短排在前面
        scheduleInfoList.sort((o1, o2) -> {
            if (o1.getDistance().compareTo(o2.getDistance()) > 0) {
                return 1;
            } else {
                return -1;
            }
        });

        for (int i = 0; i < scheduleInfoList.size(); i++) {
            ScheduleInfo info = scheduleInfoList.get(i);
            info.setIndexNo(i + 1);
            info.setStatus("0");
            if (i == 0) {
                info.setPreviousLongitude(merchantInfo.getLongitude());
                info.setPreviousLatitude(merchantInfo.getLatitude());
            } else {
                info.setPreviousLongitude(scheduleInfoList.get(i - 1).getCurrentLongitude());
                info.setPreviousLatitude(scheduleInfoList.get(i - 1).getCurrentLatitude());
            }
            if (i == scheduleInfoList.size() - 1) {
                info.setNextLongitude(merchantInfo.getLongitude());
                info.setNextLatitude(merchantInfo.getLatitude());
            } else {
                info.setNextLongitude(scheduleInfoList.get(i + 1).getCurrentLongitude());
                info.setNextLatitude(scheduleInfoList.get(i + 1).getCurrentLatitude());
            }
            this.updateById(info);
        }
        this.updateBatchById(scheduleInfoList);
        return true;
    }

    /**
     * 查询车辆调度信息
     *
     * @param scheduleCode 车辆调度编号
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> querySchedule(String scheduleCode) throws FebsException {
        // 返回数据
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        if (StrUtil.isEmpty(scheduleCode)) {
            throw new FebsException("车辆调度编号不能为空！");
        }

        List<ScheduleInfo> scheduleInfoList = this.list(Wrappers.<ScheduleInfo>lambdaQuery().eq(ScheduleInfo::getScheduleCode, scheduleCode));
        if (CollectionUtil.isEmpty(scheduleInfoList)) {
            throw new FebsException("车辆调度信息不存在！");
        }

        List<Integer> orderIds = scheduleInfoList.stream().map(ScheduleInfo::getOrderId).collect(Collectors.toList());
        // 获取订单信息
        List<OrderInfo> orderInfoList = orderInfoService.list(Wrappers.<OrderInfo>lambdaQuery().in(OrderInfo::getId, orderIds));
        Map<Integer, OrderInfo> orderMap = orderInfoList.stream().collect(Collectors.toMap(OrderInfo::getId, e -> e));
        // 获取收货地址
        List<Integer> addressIds = orderInfoList.stream().map(OrderInfo::getAddressId).collect(Collectors.toList());
        List<AddressInfo> addressInfoList = addressInfoService.list(Wrappers.<AddressInfo>lambdaQuery().in(AddressInfo::getId, addressIds));
        // 按ID转map
        Map<Integer, AddressInfo> addressMap = addressInfoList.stream().collect(Collectors.toMap(AddressInfo::getId, e -> e));
        // 获取用户信息
        List<Integer> userIds = orderInfoList.stream().map(OrderInfo::getUserId).collect(Collectors.toList());
        List<UserInfo> userInfoList = userInfoService.list(Wrappers.<UserInfo>lambdaQuery().in(UserInfo::getId, userIds));
        Map<Integer, UserInfo> userMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getId, e -> e));

        for (ScheduleInfo scheduleInfo : scheduleInfoList) {
            OrderInfo orderInfo = orderMap.get(scheduleInfo.getOrderId());
            if (orderInfo == null) {
                continue;
            }
            AddressInfo addressInfo = addressMap.get(orderInfo.getAddressId());
            UserInfo userInfo = userMap.get(orderInfo.getUserId());
            list.add(new LinkedHashMap<String, Object>() {
                {
                    put("order", orderInfo);
                    put("address", addressInfo);
                    put("user", userInfo);
                }
            });
        }
        return list;
    }
}
