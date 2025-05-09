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
 * 商品采购
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PurchaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 采购单号
     */
    private String code;

    /**
     * 供应商ID
     */
    private Integer supplierId;

    /**
     * 采购数量
     */
    private Integer amount;

    /**
     * 采购人
     */
    private String purchaser;

    /**
     * 采购时间
     */
    private String createDate;

    /**
     * 采购状态（-1.退货 1.运输中 2.已验收）
     */
    private Integer status;

    /**
     * 采购物流
     */
    private String logistics;

    /**
     * 采购商品
     */
    private String purchaseDrug;

    /**
     * 总价格
     */
    private BigDecimal totalPrice;

    /**
     * 商家ID
     */
    private Integer pharmacyId;

    @TableField(exist = false)
    private Integer merchantId;
}
