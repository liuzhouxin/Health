package com.health.record.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class ExamReportListItem implements Serializable {
    private Long id;
    private String reportNo;
    private String reportTitle;
    private Date examDate;
    private String hospital;
    private Integer abnormalCount;
    private Integer totalCount;
}
