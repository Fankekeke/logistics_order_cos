package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.entity.ScheduleInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IScheduleInfoService extends IService<ScheduleInfo> {

    /**
     * 分页获取车次记录信息
     *
     * @param page         分页对象
     * @param scheduleInfo 车次记录信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryScheduleByPage(Page<ScheduleInfo> page, ScheduleInfo scheduleInfo);

    /**
     * 订单绑定未工作车辆
     *
     * @param vehicleId 车辆ID
     * @param orderId   订单ID
     * @return 结果
     */
    boolean orderBindSchedule(Integer vehicleId, Integer orderId) throws FebsException;

    /**
     * 订单检查
     *
     * @param orderId 订单ID
     * @return 结果
     */
    boolean orderCheck(Integer orderId) throws FebsException;

    /**
     * 查询车辆调度信息
     *
     * @param scheduleCode 车辆调度编号
     * @return 结果
     */
    LinkedHashMap<String, Object> querySchedule(String scheduleCode) throws FebsException;
}
