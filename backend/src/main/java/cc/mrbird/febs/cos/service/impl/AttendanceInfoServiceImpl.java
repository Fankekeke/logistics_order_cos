package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.dao.AttendanceInfoMapper;
import cc.mrbird.febs.cos.entity.AttendanceInfo;
import cc.mrbird.febs.cos.entity.StaffInfo;
import cc.mrbird.febs.cos.service.IAttendanceInfoService;
import cc.mrbird.febs.cos.service.IStaffInfoService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 考勤管理 实现层
 *
 * @author FanK fan1ke2ke@gmail.com
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendanceInfoServiceImpl extends ServiceImpl<AttendanceInfoMapper, AttendanceInfo> implements IAttendanceInfoService {

    private final IStaffInfoService staffInfoService;

    /**
     * 分页获取考勤打卡
     *
     * @param page      分页对象
     * @param attendanceInfo 考勤打卡
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryAttendancePage(Page<AttendanceInfo> page, AttendanceInfo attendanceInfo) {
        return baseMapper.queryAttendancePage(page, attendanceInfo);
    }

    /**
     * 校验今日是否已经打卡
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public AttendanceInfo queryTodayCheck(Integer userId, String createDate) {
        return baseMapper.queryTodayCheck(userId, createDate);
    }

    /**
     * 根据员工ID查询考勤打卡
     *
     * @param staffId 员工ID
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryAttendanceByStaff(Integer staffId) {
        // 查询考勤打卡
        List<AttendanceInfo> attendanceInfoList = this.list(Wrappers.<AttendanceInfo>lambdaQuery().eq(AttendanceInfo::getStaffId, staffId));
        if (CollectionUtil.isEmpty(attendanceInfoList)) {
            return Collections.emptyList();
        }
        // 返回数据
        List<LinkedHashMap<String, Object>> result = CollectionUtil.newArrayList();
        for (AttendanceInfo attendanceInfo : attendanceInfoList) {
            // 考勤打卡
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("date", attendanceInfo.getPutTakeDate());
            map.put("putTakeDate", attendanceInfo.getPutTakeDate());
            map.put("outTakeDate", attendanceInfo.getOutTakeDate());
            result.add(map);
        }
        return result;
    }

    /**
     * 考勤打卡
     *
     * @param attendanceInfo 打卡信息
     * @return 结果
     */
    @Override
    public boolean checkWork(AttendanceInfo attendanceInfo) {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, attendanceInfo.getStaffId()));
        if (staffInfo != null) {
            attendanceInfo.setStaffId(staffInfo.getId());
        }

        // 校验本日是否打卡
        AttendanceInfo todayCheck = this.checkWorkByToday(attendanceInfo.getStaffId());
        if (todayCheck == null) {
            attendanceInfo.setPutTakeDate(DateUtil.formatDateTime(new Date()));
            return this.save(attendanceInfo);
        } else {
            attendanceInfo.setOutTakeDate(DateUtil.formatDateTime(new Date()));
            return this.updateById(attendanceInfo);
        }
    }

    /**
     * 获取当天打卡状态
     *
     * @param staffId 员工ID
     * @return 结果
     */
    @Override
    public AttendanceInfo checkWorkByToday(Integer staffId) {
        StaffInfo staffInfo = staffInfoService.getOne(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getUserId, staffId));
        if (staffInfo == null) {
            return null;
        }

        List<AttendanceInfo> attendanceInfoList = this.list(Wrappers.<AttendanceInfo>lambdaQuery().eq(AttendanceInfo::getStaffId, staffInfo.getId()));
        if (CollectionUtil.isEmpty(attendanceInfoList)) {
            return null;
        }

        for (AttendanceInfo attendanceInfo : attendanceInfoList) {
            if (DateUtil.isSameDay(new Date(), DateUtil.parseDate(attendanceInfo.getPutTakeDate()))) {
                return attendanceInfo;
            }
        }
        return null;
    }
}
