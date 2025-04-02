package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.TypeInfo;
import cc.mrbird.febs.cos.dao.TypeInfoMapper;
import cc.mrbird.febs.cos.service.ITypeInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class TypeInfoServiceImpl extends ServiceImpl<TypeInfoMapper, TypeInfo> implements ITypeInfoService {

    /**
     * 分页获取药品类型信息
     *
     * @param page              分页对象
     * @param typeInfo 药品类型信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectFirnitureTypePage(Page<TypeInfo> page, TypeInfo typeInfo) {
        return baseMapper.selectFirnitureTypePage(page, typeInfo);
    }
}
