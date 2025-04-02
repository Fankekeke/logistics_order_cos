package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.ExamineInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IExamineInfoService extends IService<ExamineInfo> {

    /**
     * 分页获取问题检查信息
     *
     * @param page         分页对象
     * @param examineInfo 问题检查信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryExaminePage(Page<ExamineInfo> page, ExamineInfo examineInfo);
}
