package com.health.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("exam_item")//体检项目
public class ExamItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String itemCode;
    private String itemName;
    private String category;
    private String unit;
    private BigDecimal referenceMin;
    private BigDecimal referenceMax;
    private String description;
    private Integer isCommon;
    private Integer sortOrder;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
