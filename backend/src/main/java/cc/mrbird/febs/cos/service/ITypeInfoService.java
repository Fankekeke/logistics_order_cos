package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.TypeInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface ITypeInfoService extends IService<TypeInfo> {

    /**
     * 分页获取药品类型信息
     *
     * @param page              分页对象
     * @param typeInfo 药品类型信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectFirnitureTypePage(Page<TypeInfo> page, TypeInfo typeInfo);
}
