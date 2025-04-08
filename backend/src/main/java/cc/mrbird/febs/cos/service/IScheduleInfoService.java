package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.entity.ScheduleInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IScheduleInfoService extends IService<ScheduleInfo> {

    /**
     * 订单绑定未工作车辆
     *
     * @param vehicleId 车辆ID
     * @param orderId   订单ID
     * @return 结果
     */
    boolean orderBindSchedule(Integer vehicleId, Integer orderId) throws FebsException;

    /**
     * 查询车辆调度信息
     *
     * @param scheduleCode 车辆调度编号
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> querySchedule(String scheduleCode) throws FebsException;
}
