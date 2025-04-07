package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 车次记录
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ScheduleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 车辆ID
     */
    private Integer vehicleId;

    /**
     * 订单编号
     */
    private Integer orderId;

    /**
     * 距离
     */
    private BigDecimal distance;

    /**
     * 配送顺序（从1开始）
     */
    private Integer indexNo;

    /**
     * 状态（0.未完成 1.已完成）
     */
    private String status;

    /**
     * 所属车次
     */
    private String scheduleCode;

    /**
     * 上一个点位经度
     */
    private BigDecimal previousLongitude;

    /**
     * 上一个点位纬度
     */
    private BigDecimal previousLatitude;

    /**
     * 下一个点位经度
     */
    private BigDecimal nextLongitude;

    /**
     * 下一个点位纬度
     */
    private BigDecimal nextLatitude;


}
