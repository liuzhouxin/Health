package com.health.warning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.common.constant.Constants;
import com.health.common.exception.BusinessException;
import com.health.common.result.Result;
import com.health.warning.entity.*;
import com.health.warning.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarningService {

    private final HealthWarningMapper warningMapper;
    private final HealthNewsMapper newsMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String WARNING_COUNT_KEY = Constants.REDIS_KEY_WARNING + "count:";
    private static final String WARNING_LIST_KEY = Constants.REDIS_KEY_WARNING + "list:";
    private static final String HOT_NEWS_KEY = Constants.REDIS_KEY_NEWS + "hot";
    private static final String NEWS_CATEGORY_KEY = Constants.REDIS_KEY_NEWS + "category:";

    public Result<WarningCountVO> getWarningCount(Long userId) {
        String cacheKey = WARNING_COUNT_KEY + userId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return Result.success((WarningCountVO) cached);
        }
        WarningCountVO count = warningMapper.selectWarningCountByUserId(userId);
        if (count == null) {
            count = new WarningCountVO();
            count.setTotal(0L);
        }
        redisTemplate.opsForValue().set(cacheKey, count, 30, TimeUnit.MINUTES);
        return Result.success(count);
    }

    public Result<List<HealthWarning>> listWarnings(Long userId, String warningLevel, Integer isHandled) {
        String cacheKey = WARNING_LIST_KEY + userId + ":" + warningLevel + ":" + isHandled;
        if (warningLevel == null && isHandled == null) {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success((List<HealthWarning>) cached);
            }
        }
        List<HealthWarning> list = warningMapper.selectWarningsByUserId(userId, warningLevel, isHandled);
        if (warningLevel == null && isHandled == null) {
            redisTemplate.opsForValue().set(cacheKey, list, 10, TimeUnit.MINUTES);
        }
        return Result.success(list);
    }

    public Result<HealthWarning> getWarningById(Long id) {
        HealthWarning warning = warningMapper.selectById(id);
        if (warning == null) {
            throw new BusinessException("预警不存在");
        }
        return Result.success(warning);
    }

    @Transactional
    public Result<HealthWarning> createWarning(HealthWarning warning) {
        warningMapper.insert(warning);
        clearWarningCache(warning.getUserId());
        return Result.success("创建成功", warning);
    }

    @Transactional
    public Result<HealthWarning> handleWarning(Long id, String handler, String handleRemark) {
        HealthWarning warning = warningMapper.selectById(id);
        if (warning == null) {
            throw new BusinessException("预警不存在");
        }
        warning.setIsHandled(1);
        warning.setHandleTime(new java.util.Date());
        warning.setHandler(handler);
        warning.setHandleRemark(handleRemark);
        warningMapper.updateById(warning);
        clearWarningCache(warning.getUserId());
        return Result.success("处理成功", warning);
    }

    @Transactional
    public Result<Void> deleteWarning(Long id) {
        HealthWarning warning = warningMapper.selectById(id);
        if (warning != null) {
            warningMapper.deleteById(id);
            clearWarningCache(warning.getUserId());
        }
        return Result.success("删除成功", null);
    }

    public Result<List<HotNewsVO>> getHotNews(int limit) {
        Object cached = redisTemplate.opsForValue().get(HOT_NEWS_KEY);
        if (cached != null) {
            return Result.success((List<HotNewsVO>) cached);
        }
        List<HotNewsVO> list = newsMapper.selectHotNews(limit);
        redisTemplate.opsForValue().set(HOT_NEWS_KEY, list, 1, TimeUnit.HOURS);
        return Result.success(list);
    }

    public Result<List<HealthNews>> listNews(String category, int limit) {
        if (category != null && !category.isEmpty()) {
            String cacheKey = NEWS_CATEGORY_KEY + category;
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success((List<HealthNews>) cached);
            }
            List<HealthNews> list = newsMapper.selectNewsByCategory(category, limit);
            redisTemplate.opsForValue().set(cacheKey, list, 1, TimeUnit.HOURS);
            return Result.success(list);
        }
        QueryWrapper<HealthNews> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByDesc("publish_time").last("LIMIT " + limit);
        return Result.success(newsMapper.selectList(wrapper));
    }

    public Result<HealthNews> getNewsById(Long id) {
        HealthNews news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException("资讯不存在");
        }
        news.setViewCount(news.getViewCount() + 1);
        newsMapper.updateById(news);
        clearNewsCache();
        return Result.success(news);
    }

    @Transactional
    public Result<HealthNews> createNews(HealthNews news) {
        newsMapper.insert(news);
        clearNewsCache();
        return Result.success("创建成功", news);
    }

    @Transactional
    public Result<HealthNews> updateNews(HealthNews news) {
        newsMapper.updateById(news);
        clearNewsCache();
        return Result.success("更新成功", news);
    }

    @Transactional
    public Result<Void> deleteNews(Long id) {
        newsMapper.deleteById(id);
        clearNewsCache();
        return Result.success("删除成功", null);
    }

    @Async
    public void asyncRefreshWarningCache(Long userId) {
        try {
            WarningCountVO count = warningMapper.selectWarningCountByUserId(userId);
            if (count == null) count = new WarningCountVO();
            redisTemplate.opsForValue().set(WARNING_COUNT_KEY + userId, count, 30, TimeUnit.MINUTES);
            log.info("异步刷新预警缓存完成, userId: {}", userId);
        } catch (Exception e) {
            log.error("异步刷新预警缓存失败", e);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void scheduledRefreshNewsCache() {
        try {
            List<HotNewsVO> list = newsMapper.selectHotNews(10);
            redisTemplate.opsForValue().set(HOT_NEWS_KEY, list, 1, TimeUnit.HOURS);
            log.info("定时刷新热门资讯缓存完成, 数量: {}", list.size());
        } catch (Exception e) {
            log.error("定时刷新热门资讯缓存失败", e);
        }
    }

    private void clearWarningCache(Long userId) {
        redisTemplate.delete(WARNING_COUNT_KEY + userId);
        redisTemplate.delete(WARNING_LIST_KEY + userId + ":null:null");
    }

    private void clearNewsCache() {
        redisTemplate.delete(HOT_NEWS_KEY);
        redisTemplate.delete(NEWS_CATEGORY_KEY);
    }
}