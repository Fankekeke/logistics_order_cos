package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.entity.MerchantInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IMerchantInfoService extends IService<MerchantInfo> {

    /**
     * 分页获取药店信息
     *
     * @param page        分页对象
     * @param merchantInfo 药店信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectMerchantPage(Page<MerchantInfo> page, MerchantInfo merchantInfo);

    /**
     * 根据药店获取订单评价信息
     *
     * @param merchantId 药店ID
     * @param dishesId   药品ID
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectEvaluateByMerchant(Integer merchantId, Integer dishesId);

    /**
     * 药店获取统计信息
     *
     * @param userId 药店用户ID
     * @return 结果
     */
    LinkedHashMap<String, Object> selectHomeDataByMerchant(Integer userId);

    /**
     * 管理员获取统计信息
     *
     * @return 结果
     */
    LinkedHashMap<String, Object> selectHomeDataByAdmin();

    /**
     * 根据月份获取药品统计情况
     *
     * @param date 日期
     * @return 结果
     */
    LinkedHashMap<String, Object> selectStatisticsByMonth(String date) throws FebsException;
}
