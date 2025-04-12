package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.LocationUtils;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.dao.ScheduleInfoMapper;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.*;
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

    @Lazy
    private final IOrderInfoService orderInfoService;

    private final IUserInfoService userInfoService;

    private final IOrderItemInfoService orderItemInfoService;

    private final IDishesInfoService dishesInfoService;

    private final TemplateEngine templateEngine;

    private final IMailService mailService;

    /**
     * 分页获取车次记录信息
     *
     * @param page         分页对象
     * @param scheduleInfo 车次记录信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryScheduleByPage(Page<ScheduleInfo> page, ScheduleInfo scheduleInfo) {
        return baseMapper.queryScheduleByPage(page, scheduleInfo);
    }

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
        // 判断此订单是否已经绑定
        if (orderInfoService.getOne(Wrappers.<OrderInfo>lambdaQuery().eq(OrderInfo::getId, orderId).eq(OrderInfo::getStatus, "2")) != null) {
            throw new FebsException("订单已绑定！");
        }

        // 获取车辆信息
        VehicleInfo vehicleInfo = vehicleInfoService.getById(vehicleId);
        if (vehicleInfo == null) {
            throw new FebsException("车辆信息不存在！");
        }

        // 获取商家地址
        MerchantInfo merchantInfo = merchantInfoService.getById(vehicleInfo.getUserId());
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
        scheduleInfo.setStatus("0");
        double distance = LocationUtils.getDistance(merchantInfo.getLongitude().doubleValue(), merchantInfo.getLatitude().doubleValue(), addressInfo.getLongitude().doubleValue(), addressInfo.getLatitude().doubleValue());
        scheduleInfo.setDistance(BigDecimal.valueOf(distance));

        // 判断车辆是否已绑定工单
        if (StrUtil.isEmpty(vehicleInfo.getScheduleCode())) {
            scheduleInfo.setIndexNo(1);
            scheduleCode = "SCH-" + System.currentTimeMillis();
            // 更新车辆车次
            vehicleInfo.setScheduleCode(scheduleCode);
            // vehicleInfo.setWorkStatus("1");
            vehicleInfoService.updateById(vehicleInfo);

            // 更新订单状态
            orderInfo.setStatus("2");
            orderInfoService.updateById(orderInfo);

            scheduleInfo.setScheduleCode(scheduleCode);
            return this.save(scheduleInfo);
        } else {
            scheduleCode = vehicleInfo.getScheduleCode();
            scheduleInfo.setScheduleCode(scheduleCode);
            this.save(scheduleInfo);
        }

        // 获取此车次信息
        List<ScheduleInfo> scheduleInfoList = this.list(Wrappers.<ScheduleInfo>lambdaQuery().eq(ScheduleInfo::getScheduleCode, scheduleCode));

        // 按照距离排序，距离最短排在前面
        scheduleInfoList = scheduleInfoList.stream().sorted(Comparator.comparing(ScheduleInfo::getDistance)).collect(Collectors.toList());

        // 设置订单状态
        orderInfo.setStatus("2");

        UserInfo userInfo = userInfoService.getById(orderInfo.getUserId());
        // 配送发送邮件
        if (StrUtil.isNotEmpty(userInfo.getMail())) {
            Context context = new Context();
            context.setVariable("today", DateUtil.formatDate(new Date()));
            context.setVariable("custom", userInfo.getName() + "，您好，在 " + merchantInfo.getName() + " 消费订单 " + orderInfo.getCode() + "，正在配送，请耐心等待");
            String emailContent = templateEngine.process("registerEmail", context);
            mailService.sendHtmlMail(userInfo.getMail(), DateUtil.formatDate(new Date()) + "配送通知", emailContent);
        }
        // 更新订单状态
        orderInfoService.updateById(orderInfo);

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
//            this.updateById(info);
        }
        this.updateBatchById(scheduleInfoList);
        return true;
    }

    /**
     * 订单检查
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    public boolean orderCheck(Integer orderId) throws FebsException {
        // 获取订单所属车次
        ScheduleInfo scheduleInfo = this.getOne(Wrappers.<ScheduleInfo>lambdaQuery().eq(ScheduleInfo::getOrderId, orderId));
        if (scheduleInfo == null) {
            throw new FebsException("订单所属车次不存在！");
        }
        // 更新车辆调度状态
        scheduleInfo.setStatus("1");
        this.updateById(scheduleInfo);
        // 获取其他订单是否完成
        List<ScheduleInfo> scheduleInfoList = this.list(Wrappers.<ScheduleInfo>lambdaQuery().eq(ScheduleInfo::getScheduleCode, scheduleInfo.getScheduleCode()));
        // 判断所有订单是否完成
        boolean hasSchedule = scheduleInfoList.stream().allMatch(info -> info.getStatus().equals("1"));
        if (hasSchedule) {
            vehicleInfoService.update(Wrappers.<VehicleInfo>lambdaUpdate().set(VehicleInfo::getWorkStatus, "0").set(VehicleInfo::getScheduleCode, null).eq(VehicleInfo::getId, scheduleInfo.getVehicleId()));
        } else {
            orderInfoService.update(Wrappers.<OrderInfo>lambdaUpdate().set(OrderInfo::getStatus, "3").eq(OrderInfo::getId, orderId));
        }
        return true;
    }

    /**
     * 查询车辆调度信息
     *
     * @param scheduleCode 车辆调度编号
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> querySchedule(String scheduleCode) throws FebsException {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
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

        // 车辆信息
        VehicleInfo vehicleInfo = vehicleInfoService.getById(scheduleInfoList.get(0).getVehicleId());
        // 所属商家
        MerchantInfo merchantInfo = merchantInfoService.getOne(Wrappers.<MerchantInfo>lambdaQuery().eq(MerchantInfo::getId, vehicleInfo.getUserId()));
        result.put("vehicle", vehicleInfo);
        result.put("merchant", merchantInfo);

        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        for (ScheduleInfo scheduleInfo : scheduleInfoList) {
            OrderInfo orderInfo = orderMap.get(scheduleInfo.getOrderId());
            if (orderInfo == null) {
                continue;
            }
            AddressInfo addressInfo = addressMap.get(orderInfo.getAddressId());
            UserInfo userInfo = userMap.get(orderInfo.getUserId());

            // 订单详情
            List<OrderItemInfo> orderItemInfoList = orderItemInfoService.list(Wrappers.<OrderItemInfo>lambdaQuery().eq(OrderItemInfo::getOrderId, orderInfo.getId()));
            // 获取商品信息
            List<Integer> dishesIds = orderItemInfoList.stream().map(OrderItemInfo::getDishesId).distinct().collect(Collectors.toList());
            List<DishesInfo> dishesInfoList = dishesInfoService.list(Wrappers.<DishesInfo>lambdaQuery().in(DishesInfo::getId, dishesIds));
            Map<Integer, DishesInfo> dishesMap = dishesInfoList.stream().collect(Collectors.toMap(DishesInfo::getId, e -> e));

            for (OrderItemInfo orderItemInfo : orderItemInfoList) {
                if (CollectionUtil.isNotEmpty(dishesMap) && dishesMap.get(orderItemInfo.getDishesId()) != null) {
                    DishesInfo dishesInfo = dishesMap.get(orderItemInfo.getDishesId());
                    orderItemInfo.setDishesName(dishesInfo.getName());
                    orderItemInfo.setImages(dishesInfo.getImages());
                    orderItemInfo.setRawMaterial(dishesInfo.getRawMaterial());
                    orderItemInfo.setPortion(dishesInfo.getPortion());
                }
            }

            list.add(new LinkedHashMap<String, Object>() {
                {
                    put("orderItem", orderItemInfoList);
                    put("item", scheduleInfo);
                    put("order", orderInfo);
                    put("address", addressInfo);
                    put("user", userInfo);
                }
            });
        }

        result.put("schedule", list);
        return result;
    }
}
