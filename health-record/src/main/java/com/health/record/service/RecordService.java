package com.health.record.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.exception.BusinessException;
import com.health.common.result.PageResult;
import com.health.common.result.Result;
import com.health.record.entity.*;
import com.health.record.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {

    private final HealthRecordMapper healthRecordMapper;
    private final ExamReportMapper examReportMapper;
    private final ExamReportItemMapper examReportItemMapper;
    private final ExamItemMapper examItemMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String EXAM_ITEM_CACHE_KEY = "health:exam:item:common";

    public Result<List<HealthRecord>> listRecordsByUser(Long userId, String recordType) {
        QueryWrapper<HealthRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (recordType != null && !recordType.isEmpty()) {
            wrapper.eq("record_type", recordType);
        }
        wrapper.orderByDesc("record_date");
        return Result.success(healthRecordMapper.selectList(wrapper));
    }

    public Result<PageResult<HealthRecord>> pageRecords(Long userId, Integer pageNo, Integer pageSize, String recordType) {
        Page<HealthRecord> page = new Page<>(pageNo, pageSize);
        QueryWrapper<HealthRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (recordType != null && !recordType.isEmpty()) {
            wrapper.eq("record_type", recordType);
        }
        wrapper.orderByDesc("record_date");
        Page<HealthRecord> result = healthRecordMapper.selectPage(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords(), pageNo, pageSize));
    }

    @Transactional
    public Result<HealthRecord> createRecord(HealthRecord record) {
        healthRecordMapper.insert(record);
        return Result.success("创建成功", record);
    }

    @Transactional
    public Result<HealthRecord> updateRecord(HealthRecord record) {
        healthRecordMapper.updateById(record);
        return Result.success("更新成功", record);
    }

    @Transactional
    public Result<Void> deleteRecord(Long id) {
        healthRecordMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    public Result<List<ExamReportListItem>> listReports(Long userId) {
        List<ExamReportListItem> list = examReportMapper.selectReportListWithAbnormalCount(userId);
        return Result.success(list);
    }

    public Result<ExamReportDetail> getReportDetail(Long reportId) {
        ExamReport report = examReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("体检报告不存在");
        }
        List<ExamReportItem> items = examReportItemMapper.selectByReportId(reportId);
        ExamReportDetail detail = new ExamReportDetail();
        detail.setReport(report);
        detail.setItems(items);
        return Result.success(detail);
    }

    @Transactional
    public Result<ExamReport> createReport(ExamReport report, List<ExamReportItem> items) {
        if (report.getReportNo() == null) {
            report.setReportNo("REP" + System.currentTimeMillis());
        }
        examReportMapper.insert(report);
        if (items != null && !items.isEmpty()) {
            for (ExamReportItem item : items) {
                item.setReportId(report.getId());
                examReportItemMapper.insert(item);
            }
        }
        return Result.success("创建成功", report);
    }

    @Transactional
    public Result<ExamReport> updateReport(ExamReport report, List<ExamReportItem> items) {
        examReportMapper.updateById(report);
        if (items != null) {
            examReportItemMapper.delete(new QueryWrapper<ExamReportItem>().eq("report_id", report.getId()));
            for (ExamReportItem item : items) {
                item.setId(null);
                item.setReportId(report.getId());
                examReportItemMapper.insert(item);
            }
        }
        return Result.success("更新成功", report);
    }

    @Transactional
    public Result<Void> deleteReport(Long reportId) {
        examReportItemMapper.delete(new QueryWrapper<ExamReportItem>().eq("report_id", reportId));
        examReportMapper.deleteById(reportId);
        return Result.success("删除成功", null);
    }

    public Result<List<ExamItem>> listExamItems(String category) {
        QueryWrapper<ExamItem> wrapper = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        wrapper.eq("status", 1).orderByAsc("sort_order");
        return Result.success(examItemMapper.selectList(wrapper));
    }

    public Result<List<ExamItem>> getCommonExamItems() {
        Object cached = redisTemplate.opsForValue().get(EXAM_ITEM_CACHE_KEY);
        if (cached != null) {
            return Result.success((List<ExamItem>) cached);
        }
        List<ExamItem> items = examItemMapper.selectCommonItems();
        redisTemplate.opsForValue().set(EXAM_ITEM_CACHE_KEY, items, 1, TimeUnit.HOURS);
        return Result.success(items);
    }

    @Async
    public void asyncCacheCommonExamItems() {
        try {
            List<ExamItem> items = examItemMapper.selectCommonItems();
            redisTemplate.opsForValue().set(EXAM_ITEM_CACHE_KEY, items, 1, TimeUnit.HOURS);
            log.info("异步缓存常用体检项目完成, 数量: {}", items.size());
        } catch (Exception e) {
            log.error("异步缓存常用体检项目失败", e);
        }
    }

    @Transactional
    public Result<ExamItem> createExamItem(ExamItem item) {
        examItemMapper.insert(item);
        redisTemplate.delete(EXAM_ITEM_CACHE_KEY);
        return Result.success("创建成功", item);
    }

    @Transactional
    public Result<ExamItem> updateExamItem(ExamItem item) {
        examItemMapper.updateById(item);
        redisTemplate.delete(EXAM_ITEM_CACHE_KEY);
        return Result.success("更新成功", item);
    }

    @Transactional
    public Result<Void> deleteExamItem(Long id) {
        examItemMapper.deleteById(id);
        redisTemplate.delete(EXAM_ITEM_CACHE_KEY);
        return Result.success("删除成功", null);
    }
}