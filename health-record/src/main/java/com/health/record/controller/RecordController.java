package com.health.record.controller;

import com.health.common.context.UserContextHolder;
import com.health.common.result.PageResult;
import com.health.common.result.Result;
import com.health.record.entity.*;
import com.health.record.service.RecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
@Api(tags = "健康档案管理")
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/list")
    @ApiOperation("健康记录列表")
    public Result<List<HealthRecord>> list(
            @RequestParam(required = false) String recordType) {
        Long userId = UserContextHolder.getUserId();
        return recordService.listRecordsByUser(userId, recordType);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询健康记录")
    public Result<PageResult<HealthRecord>> page(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String recordType) {
        Long userId = UserContextHolder.getUserId();
        return recordService.pageRecords(userId, pageNo, pageSize, recordType);
    }

    @PostMapping
    @ApiOperation("创建健康记录")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<HealthRecord> create(@RequestBody HealthRecord record) {
        record.setUserId(UserContextHolder.getUserId());
        return recordService.createRecord(record);
    }

    @PutMapping
    @ApiOperation("更新健康记录")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<HealthRecord> update(@RequestBody HealthRecord record) {
        return recordService.updateRecord(record);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除健康记录")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return recordService.deleteRecord(id);
    }

    @GetMapping("/report/list")
    @ApiOperation("体检报告列表")
    public Result<List<ExamReportListItem>> listReports() {
        return recordService.listReports(UserContextHolder.getUserId());
    }

    @GetMapping("/report/{id}")
    @ApiOperation("体检报告详情")
    public Result<ExamReportDetail> getReportDetail(@PathVariable Long id) {
        return recordService.getReportDetail(id);
    }

    @PostMapping("/report")
    @ApiOperation("创建体检报告")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<ExamReport> createReport(@RequestBody ReportCreateDTO dto) {
        ExamReport report = dto.getReport();
        report.setUserId(UserContextHolder.getUserId());
        return recordService.createReport(report, dto.getItems());
    }

    @PutMapping("/report")
    @ApiOperation("更新体检报告")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<ExamReport> updateReport(@RequestBody ReportCreateDTO dto) {
        return recordService.updateReport(dto.getReport(), dto.getItems());
    }

    @DeleteMapping("/report/{id}")
    @ApiOperation("删除体检报告")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteReport(@PathVariable Long id) {
        return recordService.deleteReport(id);
    }

    @GetMapping("/exam-item/list")
    @ApiOperation("体检项目列表")
    public Result<List<ExamItem>> listExamItems(@RequestParam(required = false) String category) {
        return recordService.listExamItems(category);
    }

    @GetMapping("/exam-item/common")
    @ApiOperation("常用体检项目(缓存)")
    public Result<List<ExamItem>> getCommonExamItems() {
        return recordService.getCommonExamItems();
    }

    @PostMapping("/exam-item")
    @ApiOperation("创建体检项目")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ExamItem> createExamItem(@RequestBody ExamItem item) {
        return recordService.createExamItem(item);
    }

    @PutMapping("/exam-item")
    @ApiOperation("更新体检项目")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ExamItem> updateExamItem(@RequestBody ExamItem item) {
        return recordService.updateExamItem(item);
    }

    @DeleteMapping("/exam-item/{id}")
    @ApiOperation("删除体检项目")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteExamItem(@PathVariable Long id) {
        return recordService.deleteExamItem(id);
    }

    @PostMapping("/exam-item/cache/refresh")
    @ApiOperation("刷新体检项目缓存")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> refreshExamItemCache() {
        recordService.asyncCacheCommonExamItems();
        return Result.success("缓存刷新中", null);
    }
}