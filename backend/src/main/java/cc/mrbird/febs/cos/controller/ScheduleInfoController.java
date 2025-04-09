package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ScheduleInfo;
import cc.mrbird.febs.cos.service.IScheduleInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/schedule-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduleInfoController {

    private final IScheduleInfoService scheduleInfoService;

    /**
     * 分页获取车次记录信息
     *
     * @param page         分页对象
     * @param scheduleInfo 车次记录信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<ScheduleInfo> page, ScheduleInfo scheduleInfo) {
        return R.ok(scheduleInfoService.queryScheduleByPage(page, scheduleInfo));
    }

    /**
     * 订单绑定未工作车辆
     *
     * @param vehicleId 车辆ID
     * @param orderId   订单ID
     * @return 结果
     */
    @GetMapping("/orderBindSchedule")
    public R orderBindSchedule(Integer vehicleId, Integer orderId) throws FebsException {
        return R.ok(scheduleInfoService.orderBindSchedule(vehicleId, orderId));
    }

    /**
     * 订单送达
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @GetMapping("/orderCheck")
    public R orderCheck(Integer orderId) throws FebsException {
        return R.ok(scheduleInfoService.orderCheck(orderId));
    }

    /**
     * 查询车次记录信息
     *
     * @param scheduleCode 车次编号
     * @return 结果
     */
    @GetMapping("/querySchedule")
    public R querySchedule(String scheduleCode) throws FebsException {
        return R.ok(scheduleInfoService.querySchedule(scheduleCode));
    }

    /**
     * 获取ID获取车次记录详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(scheduleInfoService.getById(id));
    }

    /**
     * 获取车次记录信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(scheduleInfoService.list());
    }

    /**
     * 新增车次记录信息
     *
     * @param scheduleInfo 车次记录信息
     * @return 结果
     */
    @PostMapping
    public R save(ScheduleInfo scheduleInfo) {
        return R.ok(scheduleInfoService.save(scheduleInfo));
    }

    /**
     * 修改车次记录信息
     *
     * @param scheduleInfo 车次记录信息
     * @return 结果
     */
    @PutMapping
    public R edit(ScheduleInfo scheduleInfo) {
        return R.ok(scheduleInfoService.updateById(scheduleInfo));
    }

    /**
     * 删除车次记录信息
     *
     * @param ids ids
     * @return 车次记录信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(scheduleInfoService.removeByIds(ids));
    }
}
