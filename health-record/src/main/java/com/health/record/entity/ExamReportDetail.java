package com.health.record.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class ExamReportDetail implements Serializable {
    private ExamReport report;
    private List<ExamReportItem> items;
}
