package com.health.warning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("health_warning")
public class HealthWarning implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long recordId;
    private Long reportItemId;
    private String warningType;
    private String warningLevel;
    private String warningContent;
    private String warningValue;
    private BigDecimal thresholdMin;
    private BigDecimal thresholdMax;
    private Integer isHandled;
    private Date handleTime;
    private String handler;
    private String handleRemark;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
