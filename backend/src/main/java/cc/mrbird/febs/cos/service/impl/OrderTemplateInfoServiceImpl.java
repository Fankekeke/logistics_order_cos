package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.OrderTemplateInfo;
import cc.mrbird.febs.cos.dao.OrderTemplateInfoMapper;
import cc.mrbird.febs.cos.service.IOrderTemplateInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class OrderTemplateInfoServiceImpl extends ServiceImpl<OrderTemplateInfoMapper, OrderTemplateInfo> implements IOrderTemplateInfoService {

    /**
     * 分页获取订单模板信息
     *
     * @param page              分页对象
     * @param orderTemplateInfo 订单模板信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryOrderTemplatePage(Page<OrderTemplateInfo> page, OrderTemplateInfo orderTemplateInfo) {
        return baseMapper.queryOrderTemplatePage(page, orderTemplateInfo);
    }
}
