package cc.mrbird.febs.cos.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 保质期预警
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WarrantyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 采购单号
     */
    private String purchaseCode;

    /**
     * 预警内容
     */
    private String content;

    /**
     * 药品ID
     */
    private Integer drugId;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 库存编号
     */
    private String stockCodes;

    /**
     * 商家ID
     */
    private Integer merchantId;

    @TableField(exist = false)
    private String pharmacyName;

    @TableField(exist = false)
    private String drugName;


}
