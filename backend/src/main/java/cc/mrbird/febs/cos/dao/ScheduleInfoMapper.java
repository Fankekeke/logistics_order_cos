package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.ScheduleInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface ScheduleInfoMapper extends BaseMapper<ScheduleInfo> {

    /**
     * 分页获取车次记录信息
     *
     * @param page         分页对象
     * @param scheduleInfo 车次记录信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryScheduleByPage(Page<ScheduleInfo> page, @Param("scheduleInfo") ScheduleInfo scheduleInfo);
}
