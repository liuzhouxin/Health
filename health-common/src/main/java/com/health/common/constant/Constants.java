package com.health.common.constant;

public class Constants {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_SECRET = "health-platform-secret-key-2024-jwt-secure-key";
    public static final Long JWT_EXPIRE = 86400000L;
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";
    public static final String ROLES_HEADER = "X-Roles";

    public static final String REDIS_KEY_PREFIX = "health:";
    public static final String REDIS_KEY_WARNING = REDIS_KEY_PREFIX + "warning:";
    public static final String REDIS_KEY_NEWS = REDIS_KEY_PREFIX + "news:";
    public static final String REDIS_KEY_EXAM_ITEM = REDIS_KEY_PREFIX + "exam:item:";
    public static final String REDIS_KEY_TOKEN = REDIS_KEY_PREFIX + "token:";

    public static final Integer CACHE_EXPIRE_HOURS = 24;
    public static final Integer CACHE_EXPIRE_MINUTES = 60;
}