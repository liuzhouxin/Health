package com.health.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("exam_report_item")
public class ExamReportItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reportId;
    private Long itemId;
    private String itemName;
    private String itemValue;
    private String unit;
    private String referenceRange;
    private Integer isAbnormal;
    private String abnormalLevel;
    private String remark;
}
