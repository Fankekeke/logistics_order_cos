package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.WarrantyInfo;
import cc.mrbird.febs.cos.dao.WarrantyInfoMapper;
import cc.mrbird.febs.cos.service.IWarrantyInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class WarrantyInfoServiceImpl extends ServiceImpl<WarrantyInfoMapper, WarrantyInfo> implements IWarrantyInfoService {

    /**
     * 分页获取保质期预警信息
     *
     * @param page         分页对象
     * @param warrantyInfo 保质期预警信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryWarrantyPage(Page<WarrantyInfo> page, WarrantyInfo warrantyInfo) {
        return baseMapper.queryWarrantyPage(page, warrantyInfo);
    }
}
