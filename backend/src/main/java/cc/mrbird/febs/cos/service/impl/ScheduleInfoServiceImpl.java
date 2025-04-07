package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.ScheduleInfo;
import cc.mrbird.febs.cos.dao.ScheduleInfoMapper;
import cc.mrbird.febs.cos.service.IScheduleInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author FanK
 */
@Service
public class ScheduleInfoServiceImpl extends ServiceImpl<ScheduleInfoMapper, ScheduleInfo> implements IScheduleInfoService {

    /**
     * 订单绑定未工作车辆
     *
     * @param vehicleId 车辆ID
     * @param orderId   订单ID
     * @return 结果
     */
    @Override
    public boolean orderBindSchedule(Integer vehicleId, Integer orderId) {
        return false;
    }
}
