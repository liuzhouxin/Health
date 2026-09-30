package com.health.record.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data//体检报告列表项目
public class ExamReportListItem implements Serializable {
    private Long id;
    private String reportNo;
    private String reportTitle;
    private Date examDate;
    private String hospital;
    private Integer abnormalCount;//异常项目数
    private Integer totalCount;
}
