package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 考勤管理
 *
 * @author FanK fan1ke2ke@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AttendanceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属员工
     */
    private Integer staffId;

    /**
     * 上班打卡时间
     */
    private String putTakeDate;

    /**
     * 下班打卡时间
     */
    private String outTakeDate;

    @TableField(exist = false)
    private String staffName;

    @TableField(exist = false)
    private Integer enterpriseId;
    @TableField(exist = false)
    private Integer merchantId;
}
