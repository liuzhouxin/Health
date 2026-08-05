package com.health.warning.controller;

import com.health.common.context.UserContextHolder;
import com.health.common.result.Result;
import com.health.warning.entity.*;
import com.health.warning.service.WarningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warning")
@RequiredArgsConstructor
@Api(tags = "预警与资讯管理")
public class WarningController {

    private final WarningService warningService;

    @GetMapping("/count")
    @ApiOperation("获取预警统计")
    public Result<WarningCountVO> getWarningCount() {
        return warningService.getWarningCount(UserContextHolder.getUserId());
    }

    @GetMapping("/list")
    @ApiOperation("预警列表")
    public Result<List<HealthWarning>> listWarnings(
            @RequestParam(required = false) String warningLevel,
            @RequestParam(required = false) Integer isHandled) {
        return warningService.listWarnings(UserContextHolder.getUserId(), warningLevel, isHandled);
    }

    @GetMapping("/{id}")
    @ApiOperation("预警详情")
    public Result<HealthWarning> getById(@PathVariable Long id) {
        return warningService.getWarningById(id);
    }

    @PostMapping
    @ApiOperation("创建预警")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<HealthWarning> create(@RequestBody HealthWarning warning) {
        warning.setUserId(UserContextHolder.getUserId());
        return warningService.createWarning(warning);
    }

    @PutMapping("/handle/{id}")
    @ApiOperation("处理预警")
    public Result<HealthWarning> handle(@PathVariable Long id,
                                        @RequestBody(required = false) HandleRequest req) {
        String handler = UserContextHolder.getUsername();
        String remark = req != null ? req.getHandleRemark() : null;
        return warningService.handleWarning(id, handler, remark);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除预警")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return warningService.deleteWarning(id);
    }

    @GetMapping("/news/hot")
    @ApiOperation("热门资讯列表(缓存)")
    public Result<List<HotNewsVO>> getHotNews(
            @RequestParam(defaultValue = "10") int limit) {
        return warningService.getHotNews(limit);
    }

    @GetMapping("/news/list")
    @ApiOperation("资讯列表")
    public Result<List<HealthNews>> listNews(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "20") int limit) {
        return warningService.listNews(category, limit);
    }

    @GetMapping("/news/{id}")
    @ApiOperation("资讯详情")
    public Result<HealthNews> getNews(@PathVariable Long id) {
        return warningService.getNewsById(id);
    }

    @PostMapping("/news")
    @ApiOperation("创建资讯")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<HealthNews> createNews(@RequestBody HealthNews news) {
        return warningService.createNews(news);
    }

    @PutMapping("/news")
    @ApiOperation("更新资讯")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<HealthNews> updateNews(@RequestBody HealthNews news) {
        return warningService.updateNews(news);
    }

    @DeleteMapping("/news/{id}")
    @ApiOperation("删除资讯")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteNews(@PathVariable Long id) {
        return warningService.deleteNews(id);
    }

    @PostMapping("/cache/refresh")
    @ApiOperation("刷新预警缓存")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> refreshWarningCache() {
        warningService.asyncRefreshWarningCache(UserContextHolder.getUserId());
        return Result.success("缓存刷新中", null);
    }
}

@lombok.Data
class HandleRequest {
    private String handleRemark;
}