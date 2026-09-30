package com.health.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("exam_report_item")//体检报告详情项目
public class ExamReportItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reportId;
    private Long itemId;
    private String itemName;
    private String itemValue;//检测结果
    private String unit;//单位
    private String referenceRange;//参考范围
    private Integer isAbnormal;
    private String abnormalLevel;
    private String remark;
}
