package com.health.netty.service;

import com.alibaba.fastjson.JSONObject;
import com.health.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class DataProcessService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ExecutorService processExecutor;
    private final ExecutorService warnExecutor;
    private final AtomicLong counter = new AtomicLong(0);
    private final BlockingQueue<JSONObject> dataQueue = new LinkedBlockingQueue<>(10000);

    public DataProcessService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        int processors = Runtime.getRuntime().availableProcessors();
        this.processExecutor = Executors.newFixedThreadPool(processors * 2, r -> {
            Thread t = new Thread(r, "data-process");
            t.setDaemon(true);
            return t;
        });
        this.warnExecutor = Executors.newFixedThreadPool(processors, r -> {
            Thread t = new Thread(r, "warning-process");
            t.setDaemon(true);
            return t;
        });
        startConsumer();
    }

    private void startConsumer() {
        Thread consumerThread = new Thread(() -> {
            log.info("Netty数据消费线程启动");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    JSONObject data = dataQueue.take();
                    processExecutor.submit(() -> {
                        try {
                            doProcessData(data);
                        } catch (Exception e) {
                            log.error("处理数据异常", e);
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "data-consumer");
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    public void processData(JSONObject data) {
        counter.incrementAndGet();
        dataQueue.offer(data);
        log.debug("数据已入队, 当前队列大小: {}", dataQueue.size());
    }

    private void doProcessData(JSONObject data) {
        try {
            String userId = data.getString("userId");
            String recordType = data.getString("recordType");
            String value = data.getString("value");

            JSONObject record = new JSONObject();
            record.put("userId", userId);
            record.put("recordType", recordType);
            record.put("value", value);
            record.put("timestamp", System.currentTimeMillis());
            record.put("source", "netty");

            String key = Constants.REDIS_KEY_PREFIX + "record:" + userId + ":" + recordType;
            redisTemplate.opsForList().leftPush(key, record.toJSONString());
            redisTemplate.opsForList().trim(key, 0, 999);
            redisTemplate.expire(key, 1, java.util.concurrent.TimeUnit.DAYS);

            checkAndWarn(userId, recordType, value);

            log.info("Netty数据处理完成: userId={}, type={}, value={}", userId, recordType, value);
        } catch (Exception e) {
            log.error("Netty数据处理失败", e);
        }
    }

    private void checkAndWarn(String userId, String recordType, String value) {
        warnExecutor.submit(() -> {
            try {
                double val = Double.parseDouble(value);
                String warningLevel = null;
                String warningContent = null;

                switch (recordType) {
                    case "blood_pressure_systolic":
                        if (val > 140) { warningLevel = "high"; warningContent = "收缩压过高: " + val + " mmHg"; }
                        else if (val < 90) { warningLevel = "medium"; warningContent = "收缩压过低: " + val + " mmHg"; }
                        break;
                    case "blood_pressure_diastolic":
                        if (val > 90) { warningLevel = "high"; warningContent = "舒张压过高: " + val + " mmHg"; }
                        else if (val < 60) { warningLevel = "medium"; warningContent = "舒张压过低: " + val + " mmHg"; }
                        break;
                    case "blood_sugar":
                        if (val > 7.0) { warningLevel = "high"; warningContent = "血糖过高: " + val + " mmol/L"; }
                        else if (val < 3.9) { warningLevel = "medium"; warningContent = "血糖过低: " + val + " mmol/L"; }
                        break;
                    case "heart_rate":
                        if (val > 100) { warningLevel = "medium"; warningContent = "心率过快: " + val + " 次/分"; }
                        else if (val < 60) { warningLevel = "medium"; warningContent = "心率过慢: " + val + " 次/分"; }
                        break;
                    case "temperature":
                        if (val > 37.3) { warningLevel = "high"; warningContent = "体温过高: " + val + " ℃"; }
                        break;
                    case "spo2":
                        if (val < 95) { warningLevel = "critical"; warningContent = "血氧过低: " + val + "%"; }
                        break;
                }

                if (warningLevel != null) {
                    JSONObject warning = new JSONObject();
                    warning.put("userId", userId);
                    warning.put("warningType", recordType);
                    warning.put("warningLevel", warningLevel);
                    warning.put("warningContent", warningContent);
                    warning.put("warningValue", value);
                    warning.put("createTime", System.currentTimeMillis());

                    String warnKey = Constants.REDIS_KEY_WARNING + "pending:" + userId;
                    redisTemplate.opsForList().rightPush(warnKey, warning.toJSONString());
                    log.warn("健康预警触发: userId={}, level={}, content={}", userId, warningLevel, warningContent);
                }
            } catch (Exception e) {
                log.error("预警检查异常", e);
            }
        });
    }

    @Async
    public void asyncBatchProcess(java.util.List<JSONObject> batchData) {
        for (JSONObject data : batchData) {
            processData(data);
        }
    }

    public long getProcessedCount() {
        return counter.get();
    }

    public int getQueueSize() {
        return dataQueue.size();
    }

    @PreDestroy
    public void destroy() {
        processExecutor.shutdown();
        warnExecutor.shutdown();
        log.info("Netty数据处理线程池已关闭, 总处理量: {}", counter.get());
    }
}