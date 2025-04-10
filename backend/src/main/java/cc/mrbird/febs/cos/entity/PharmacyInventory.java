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
 * 商家库存
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PharmacyInventory implements Serializable {

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
     * 库存数量
     */
    private Integer reserve;

    /**
     * 上架状态（1.上架中 2.下架 3.已售出）
     */
    private Integer shelfStatus;

    /**
     * 保质期-开始
     */
    private String startDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 保质期-结束
     */
    private String endDate;

    /**
     * 采购单号
     */
    private String purchaseCode;

    /**
     * 记录ID
     */
    private String inventoryCode;

    @TableField(exist = false)
    private String pharmacyName;

    @TableField(exist = false)
    private String drugName;

    @TableField(exist = false)
    private String rawMaterial;

    @TableField(exist = false)
    private BigDecimal unitPrice;

    @TableField(exist = false)
    private BigDecimal receiveUnitPrice;

    @TableField(exist = false)
    private String images;
    @TableField(exist = false)
    private Integer merchantId;

}
