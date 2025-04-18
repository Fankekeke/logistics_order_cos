package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 库存统计
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class InventoryStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商家ID
     */
    private Integer pharmacyId;

    /**
     * 商品ID
     */
    private Integer drugId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 出入库（1.出库 2.入库）
     */
    private Integer storageType;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 保管人
     */
    private String custodian;
    private String inventoryCode;

    @TableField(exist = false)
    private String drugName;

    @TableField(exist = false)
    private String pharmacyName;

    @TableField(exist = false)
    private BigDecimal unitPrice;

}
