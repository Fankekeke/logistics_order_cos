package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.TypeInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface TypeInfoMapper extends BaseMapper<TypeInfo> {

    /**
     * 分页获取商品类型信息
     *
     * @param page              分页对象
     * @param typeInfo 商品类型信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectFirnitureTypePage(Page<TypeInfo> page, @Param("typeInfo") TypeInfo typeInfo);
}
