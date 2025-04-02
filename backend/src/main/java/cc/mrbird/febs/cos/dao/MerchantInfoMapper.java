package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.MerchantInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface MerchantInfoMapper extends BaseMapper<MerchantInfo> {

    /**
     * 分页获取药店信息
     *
     * @param page        分页对象
     * @param merchantInfo 药店信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectMerchantPage(Page<MerchantInfo> page, @Param("merchantInfo") MerchantInfo merchantInfo);

    /**
     * 根据药店获取订单评价信息
     *
     * @param merchantId 药店ID
     * @param dishesId   药品ID
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectEvaluateByMerchant(@Param("merchantId") Integer merchantId, @Param("dishesId") Integer dishesId);
}
