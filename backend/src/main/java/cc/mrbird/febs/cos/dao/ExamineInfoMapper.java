package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.ExamineInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface ExamineInfoMapper extends BaseMapper<ExamineInfo> {

    /**
     * 分页获取问题检查信息
     *
     * @param page         分页对象
     * @param examineInfo 问题检查信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryExaminePage(Page<ExamineInfo> page, @Param("examineInfo") ExamineInfo examineInfo);
}
