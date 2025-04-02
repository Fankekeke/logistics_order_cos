package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.AttendanceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * 考勤管理 mapper层
 *
 * @author FanK fan1ke2ke@gmail.com
 */
public interface AttendanceInfoMapper extends BaseMapper<AttendanceInfo> {

    /**
     * 分页获取考勤打卡
     *
     * @param page      分页对象
     * @param attendanceInfo 考勤打卡
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryAttendancePage(Page<AttendanceInfo> page, @Param("attendanceInfo") AttendanceInfo attendanceInfo);

    /**
     * 校验今日是否已经打卡
     * @param userId 用户ID
     * @return 结果
     */
    AttendanceInfo queryTodayCheck(@Param("userId") Integer userId, @Param("createDate") String createDate);
}
