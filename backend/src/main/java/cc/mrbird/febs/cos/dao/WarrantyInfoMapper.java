package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.WarrantyInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface WarrantyInfoMapper extends BaseMapper<WarrantyInfo> {

    /**
     * 分页获取保质期预警信息
     *
     * @param page         分页对象
     * @param warrantyInfo 保质期预警信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryWarrantyPage(Page<WarrantyInfo> page, @Param("warrantyInfo") WarrantyInfo warrantyInfo);

    /**
     * 分页获取保质期预警信息
     *
     * @param page         分页对象
     * @param warrantyInfo 保质期预警信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryWarrantyPageTe4st(Page<WarrantyInfo> page, @Param("warrantyInfo") WarrantyInfo warrantyInfo);
}
