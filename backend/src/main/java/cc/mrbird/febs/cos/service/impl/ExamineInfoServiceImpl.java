package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.ExamineInfo;
import cc.mrbird.febs.cos.dao.ExamineInfoMapper;
import cc.mrbird.febs.cos.service.IExamineInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class ExamineInfoServiceImpl extends ServiceImpl<ExamineInfoMapper, ExamineInfo> implements IExamineInfoService {

    /**
     * 分页获取问题检查信息
     *
     * @param page         分页对象
     * @param examineInfo 问题检查信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryExaminePage(Page<ExamineInfo> page, ExamineInfo examineInfo) {
        return baseMapper.queryExaminePage(page, examineInfo);
    }
}
