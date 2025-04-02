package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.OrderTemplateInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IOrderTemplateInfoService extends IService<OrderTemplateInfo> {

    /**
     * 分页获取订单模板信息
     *
     * @param page              分页对象
     * @param orderTemplateInfo 订单模板信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryOrderTemplatePage(Page<OrderTemplateInfo> page, OrderTemplateInfo orderTemplateInfo);
}
