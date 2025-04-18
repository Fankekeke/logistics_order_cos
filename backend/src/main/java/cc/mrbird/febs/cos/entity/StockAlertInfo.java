package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 库存预警
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StockAlertInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商家ID
     */
    private Integer shopId;

    /**
     * 商品ID
     */
    private Integer durgId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 已读状态（0.未读 1.已读）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 商家名称
     */
    @TableField(exist = false)
    private String shopName;

    @TableField(exist = false)
    private Integer merchantId;
}
