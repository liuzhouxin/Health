package com.health.record.controller;

import com.health.record.entity.ExamReport;
import com.health.record.entity.ExamReportItem;
import lombok.Data;
import java.util.List;

@Data
public class ReportCreateDTO {
    private ExamReport report;
    private List<ExamReportItem> items;
}