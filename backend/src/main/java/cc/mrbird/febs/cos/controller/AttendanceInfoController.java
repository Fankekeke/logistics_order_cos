package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AttendanceInfo;
import cc.mrbird.febs.cos.entity.StaffInfo;
import cc.mrbird.febs.cos.service.IAttendanceInfoService;
import cc.mrbird.febs.cos.service.IStaffInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 考勤管理 控制层
 *
 * @author FanK fan1ke2ke@gmail.com
 */
@RestController
@RequestMapping("/cos/attendance-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendanceInfoController {

    private final IAttendanceInfoService attendanceInfoService;

    private final IStaffInfoService staffInfoService;

    /**
     * 分页获取考勤打卡
     *
     * @param page           分页对象
     * @param attendanceInfo 考勤打卡
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AttendanceInfo> page, AttendanceInfo attendanceInfo) {
        return R.ok(attendanceInfoService.queryAttendancePage(page, attendanceInfo));
    }

    /**
     * 查询当天打卡状态
     * @param userId 员工ID
     * @return 结果
     */
    @GetMapping("/queryTodayCheck")
    public R queryTodayCheck(Integer userId) {
        return R.ok(attendanceInfoService.queryTodayCheck(userId, DateUtil.formatDate(new Date())));
    }

    /**
     * 查询考勤打卡详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(attendanceInfoService.getById(id));
    }

    /**
     * 根据员工ID查询考勤打卡
     *
     * @param staffId 员工ID
     * @return 结果
     */
    @GetMapping("/queryAttendanceByStaff")
    public R queryAttendanceByStaff(@RequestParam(value = "staffId") Integer staffId) {
        return R.ok(attendanceInfoService.queryAttendanceByStaff(staffId));
    }

    /**
     * 查询考勤打卡列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(attendanceInfoService.list());
    }

    /**
     * 新增考勤打卡
     *
     * @param attendanceInfo 考勤打卡
     * @return 结果
     */
    @PostMapping
    public R save(AttendanceInfo attendanceInfo) {
        // 获取员工信息
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, attendanceInfo.getStaffId()));
        if (staffInfo != null) {
            attendanceInfo.setStaffId(staffInfo.getId());
        }
        attendanceInfo.setPutTakeDate(DateUtil.formatDateTime(new Date()));
        return R.ok(attendanceInfoService.checkWork(attendanceInfo));
    }

    /**
     * 获取当天打卡状态
     *
     * @param staffId 员工ID
     * @return 结果
     */
    @GetMapping("/checkWorkByToday")
    public R checkWorkByToday(@RequestParam(value = "staffId") Integer staffId) {
        return R.ok(attendanceInfoService.checkWorkByToday(staffId));
    }

    /**
     * 根据用户获取打卡记录
     *
     * @param userId 用户id
     * @return 结果
     */
    @GetMapping("/queryAttendanceRecordByUserId")
    public R queryAttendanceRecordByUserId(Integer userId) {
        // 获取员工信息
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, userId));
        if (staffInfo != null) {
            return R.ok(attendanceInfoService.list(Wrappers.<AttendanceInfo>lambdaQuery().eq(AttendanceInfo::getStaffId, staffInfo.getId())));
        } else {
            return R.ok(Collections.emptyList());
        }
    }

    /**
     * 修改考勤打卡
     *
     * @param attendanceInfo 考勤打卡
     * @return 结果
     */
    @PutMapping
    public R edit(AttendanceInfo attendanceInfo) {
        attendanceInfo.setOutTakeDate(DateUtil.formatDateTime(new Date()));
        return R.ok(attendanceInfoService.updateById(attendanceInfo));
    }

    /**
     * 删除考勤打卡
     *
     * @param ids ids
     * @return 考勤打卡
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(attendanceInfoService.removeByIds(ids));
    }
}
