package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.AttendanceInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 考勤管理 service层
 *
 * @author FanK fan1ke2ke@gmail.com
 */
public interface IAttendanceInfoService extends IService<AttendanceInfo> {

    /**
     * 分页获取考勤打卡
     *
     * @param page           分页对象
     * @param attendanceInfo 考勤打卡
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryAttendancePage(Page<AttendanceInfo> page, AttendanceInfo attendanceInfo);

    /**
     * 校验今日是否已经打卡
     * @param userId 用户ID
     * @return 结果
     */
    AttendanceInfo queryTodayCheck(Integer userId, String createDate);

    /**
     * 根据员工ID查询考勤打卡
     *
     * @param staffId 员工ID
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> queryAttendanceByStaff(Integer staffId);

    /**
     * 考勤打卡
     *
     * @param attendanceInfo 打卡信息
     * @return 结果
     */
    boolean checkWork(AttendanceInfo attendanceInfo);

    /**
     * 获取当天打卡状态
     *
     * @param staffId 员工ID
     * @return 结果
     */
    AttendanceInfo checkWorkByToday(Integer staffId);
}
