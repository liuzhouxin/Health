package com.health.netty.controller;

import com.alibaba.fastjson.JSONObject;
import com.health.common.context.UserContextHolder;
import com.health.common.result.Result;
import com.health.netty.service.DataProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/netty")
@RequiredArgsConstructor
@Api(tags = "Netty数据上报")
public class NettyController {

    private final DataProcessService dataProcessService;

    @PostMapping("/data")
    @ApiOperation("上报健康数据(HTTP)")
    public Result<Map<String, Object>> submitData(@RequestBody JSONObject data) {
        Long userId = UserContextHolder.getUserId();
        if (userId != null) {
            data.put("userId", userId);
        }
        data.put("type", "DATA");
        dataProcessService.processData(data);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "数据已提交到异步处理队列");
        result.put("queueSize", dataProcessService.getQueueSize());
        return Result.success(result);
    }

    @PostMapping("/batch")
    @ApiOperation("批量上报健康数据")
    public Result<Void> batchSubmit(@RequestBody List<JSONObject> batchData) {
        Long userId = UserContextHolder.getUserId();
        for (JSONObject data : batchData) {
            if (userId != null) data.put("userId", userId);
            data.put("type", "DATA");
        }
        dataProcessService.asyncBatchProcess(batchData);
        return Result.success("批量数据已提交", null);
    }

    @GetMapping("/stats")
    @ApiOperation("服务状态统计")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("processedCount", dataProcessService.getProcessedCount());
        stats.put("queueSize", dataProcessService.getQueueSize());
        return Result.success(stats);
    }
}