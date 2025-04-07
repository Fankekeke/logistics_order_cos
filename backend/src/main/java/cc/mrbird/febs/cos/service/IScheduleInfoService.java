package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.ScheduleInfo;
import com.baomidou.mybatisplus.extension.service.IService;

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
    boolean orderBindSchedule(Integer vehicleId, Integer orderId);
}
