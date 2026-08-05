-- =============================================
-- 智慧健康管理系统 数据库建表脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- =============================================

CREATE DATABASE IF NOT EXISTS health_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE health_platform;

-- ======================== 系统管理模块 ========================

-- 系统用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    username        VARCHAR(50)     NOT NULL                 COMMENT '用户名',
    password        VARCHAR(100)    NOT NULL                 COMMENT '密码(BCrypt加密)',
    real_name       VARCHAR(50)     DEFAULT NULL             COMMENT '真实姓名',
    phone           VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    email           VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    avatar          VARCHAR(255)    DEFAULT NULL             COMMENT '头像URL',
    gender          TINYINT         DEFAULT 0                COMMENT '性别 0:未知 1:男 2:女',
    age             INT             DEFAULT NULL             COMMENT '年龄',
    height          DECIMAL(5,1)    DEFAULT NULL             COMMENT '身高(cm)',
    weight          DECIMAL(5,1)    DEFAULT NULL             COMMENT '体重(kg)',
    status          TINYINT         DEFAULT 1                COMMENT '状态 1:启用 0:禁用',
    last_login_time DATETIME        DEFAULT NULL             COMMENT '最后登录时间',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                COMMENT '逻辑删除 0:未删除 1:已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 系统角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    role_code       VARCHAR(50)     NOT NULL                 COMMENT '角色编码',
    role_name       VARCHAR(50)     NOT NULL                 COMMENT '角色名称',
    description    VARCHAR(255)    DEFAULT NULL             COMMENT '描述',
    sort_order      INT             DEFAULT 0                COMMENT '排序',
    status          TINYINT         DEFAULT 1                COMMENT '状态 1:启用 0:禁用',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 系统权限表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    permission_code VARCHAR(100)    NOT NULL                 COMMENT '权限编码',
    permission_name VARCHAR(50)     NOT NULL                 COMMENT '权限名称',
    resource_type   VARCHAR(20)     NOT NULL                 COMMENT '资源类型 menu/button/api',
    parent_id       BIGINT          DEFAULT 0                COMMENT '父级ID',
    sort_order      INT             DEFAULT 0                COMMENT '排序',
    path            VARCHAR(255)    DEFAULT NULL             COMMENT '路径',
    icon            VARCHAR(100)    DEFAULT NULL             COMMENT '图标',
    status          TINYINT         DEFAULT 1                COMMENT '状态 1:启用 0:禁用',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (permission_code),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                 COMMENT '用户ID',
    role_id         BIGINT          NOT NULL                 COMMENT '角色ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    role_id         BIGINT          NOT NULL                 COMMENT '角色ID',
    permission_id   BIGINT          NOT NULL                 COMMENT '权限ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ======================== 健康档案模块 ========================

-- 健康档案分类表
DROP TABLE IF EXISTS health_archive_category;
CREATE TABLE health_archive_category (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    category_name   VARCHAR(100)    NOT NULL                 COMMENT '分类名称',
    category_code   VARCHAR(50)     NOT NULL                 COMMENT '分类编码',
    description     VARCHAR(255)    DEFAULT NULL             COMMENT '描述',
    icon            VARCHAR(100)    DEFAULT NULL             COMMENT '图标',
    sort_order      INT             DEFAULT 0                COMMENT '排序',
    status          TINYINT         DEFAULT 1                COMMENT '状态 1:启用 0:禁用',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_code (category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案分类表';

-- 健康记录表
DROP TABLE IF EXISTS health_record;
CREATE TABLE health_record (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                 COMMENT '用户ID',
    category_id     BIGINT          NOT NULL                 COMMENT '档案分类ID',
    title           VARCHAR(200)    NOT NULL                 COMMENT '记录标题',
    record_type     VARCHAR(50)     NOT NULL                 COMMENT '记录类型(blood_pressure/blood_sugar/heart_rate等)',
    record_value    TEXT            DEFAULT NULL             COMMENT '记录值(可存JSON数据)',
    record_date     DATE            NOT NULL                 COMMENT '记录日期',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '备注',
    status          TINYINT         DEFAULT 1                COMMENT '状态 1:正常 0:异常',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_category_id (category_id),
    KEY idx_record_date (record_date),
    KEY idx_record_type (record_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录表';

-- ======================== 体检报告模块 ========================

-- 体检项目字典表
DROP TABLE IF EXISTS exam_item;
CREATE TABLE exam_item (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    item_code       VARCHAR(50)     NOT NULL                 COMMENT '项目编码',
    item_name       VARCHAR(100)    NOT NULL                 COMMENT '项目名称',
    category        VARCHAR(50)     DEFAULT NULL             COMMENT '分类',
    unit            VARCHAR(50)     DEFAULT NULL             COMMENT '单位',
    reference_min   DECIMAL(10,2)   DEFAULT NULL             COMMENT '参考范围下限',
    reference_max   DECIMAL(10,2)   DEFAULT NULL             COMMENT '参考范围上限',
    description     VARCHAR(255)    DEFAULT NULL             COMMENT '描述',
    is_common       TINYINT         DEFAULT 0                COMMENT '是否常用 1:是 0:否',
    sort_order      INT             DEFAULT 0                COMMENT '排序',
    status          TINYINT         DEFAULT 1                COMMENT '状态 1:启用 0:禁用',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_item_code (item_code),
    KEY idx_category (category),
    KEY idx_is_common (is_common)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检项目字典表';

-- 体检报告表
DROP TABLE IF EXISTS exam_report;
CREATE TABLE exam_report (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                 COMMENT '用户ID',
    report_no       VARCHAR(50)     NOT NULL                 COMMENT '报告编号',
    report_title    VARCHAR(200)    NOT NULL                 COMMENT '报告标题',
    exam_date       DATE            NOT NULL                 COMMENT '体检日期',
    hospital        VARCHAR(200)    DEFAULT NULL             COMMENT '体检机构',
    doctor          VARCHAR(50)     DEFAULT NULL             COMMENT '体检医生',
    summary        TEXT            DEFAULT NULL             COMMENT '体检摘要',
    conclusion     TEXT            DEFAULT NULL             COMMENT '体检结论',
    suggestion     TEXT            DEFAULT NULL             COMMENT '医生建议',
    status          TINYINT         DEFAULT 1                COMMENT '状态 1:正常 0:异常',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_report_no (report_no),
    KEY idx_user_id (user_id),
    KEY idx_exam_date (exam_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检报告表';

-- 体检报告明细表
DROP TABLE IF EXISTS exam_report_item;
CREATE TABLE exam_report_item (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    report_id       BIGINT          NOT NULL                 COMMENT '报告ID',
    item_id         BIGINT          DEFAULT NULL             COMMENT '体检项目ID',
    item_name       VARCHAR(100)    NOT NULL                 COMMENT '项目名称',
    item_value      VARCHAR(100)    NOT NULL                 COMMENT '项目值',
    unit            VARCHAR(50)     DEFAULT NULL             COMMENT '单位',
    reference_range VARCHAR(200)    DEFAULT NULL             COMMENT '参考范围',
    is_abnormal     TINYINT         DEFAULT 0                COMMENT '是否异常 1:异常 0:正常',
    abnormal_level  VARCHAR(20)     DEFAULT NULL             COMMENT '异常等级 mild/moderate/severe',
    remark          VARCHAR(255)    DEFAULT NULL             COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_report_id (report_id),
    KEY idx_item_id (item_id),
    KEY idx_is_abnormal (is_abnormal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检报告明细表';

-- ======================== 预警模块 ========================

-- 健康预警表
DROP TABLE IF EXISTS health_warning;
CREATE TABLE health_warning (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                 COMMENT '用户ID',
    record_id       BIGINT          DEFAULT NULL             COMMENT '关联健康记录ID',
    report_item_id  BIGINT          DEFAULT NULL             COMMENT '关联体检报告明细ID',
    warning_type    VARCHAR(50)     NOT NULL                 COMMENT '预警类型(blood_pressure/blood_sugar等)',
    warning_level   VARCHAR(20)     NOT NULL                 COMMENT '预警等级 low/medium/high/critical',
    warning_content VARCHAR(500)    NOT NULL                 COMMENT '预警内容',
    warning_value   VARCHAR(100)    DEFAULT NULL             COMMENT '触发预警的值',
    threshold_min   DECIMAL(10,2)   DEFAULT NULL             COMMENT '阈值下限',
    threshold_max   DECIMAL(10,2)   DEFAULT NULL             COMMENT '阈值上限',
    is_handled      TINYINT         DEFAULT 0                COMMENT '是否已处理 0:未处理 1:已处理',
    handle_time     DATETIME        DEFAULT NULL             COMMENT '处理时间',
    handler         VARCHAR(50)     DEFAULT NULL             COMMENT '处理人',
    handle_remark   VARCHAR(500)    DEFAULT NULL             COMMENT '处理备注',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_warning_level (warning_level),
    KEY idx_is_handled (is_handled),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康预警表';

-- ======================== 健康资讯模块 ========================

-- 健康资讯表
DROP TABLE IF EXISTS health_news;
CREATE TABLE health_news (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    title           VARCHAR(200)    NOT NULL                 COMMENT '标题',
    summary         VARCHAR(500)    DEFAULT NULL             COMMENT '摘要',
    content         MEDIUMTEXT      DEFAULT NULL             COMMENT '正文内容',
    cover_image     VARCHAR(255)    DEFAULT NULL             COMMENT '封面图',
    author          VARCHAR(50)     DEFAULT NULL             COMMENT '作者',
    source          VARCHAR(100)    DEFAULT NULL             COMMENT '来源',
    category        VARCHAR(50)     DEFAULT NULL             COMMENT '分类',
    tags            VARCHAR(500)    DEFAULT NULL             COMMENT '标签(逗号分隔)',
    view_count      INT             DEFAULT 0                COMMENT '浏览量',
    like_count      INT             DEFAULT 0                COMMENT '点赞数',
    is_top          TINYINT         DEFAULT 0                COMMENT '是否置顶 1:是 0:否',
    is_hot          TINYINT         DEFAULT 0                COMMENT '是否热门 1:是 0:否',
    publish_time    DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    status          TINYINT         DEFAULT 1                COMMENT '状态 1:已发布 0:草稿',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category (category),
    KEY idx_publish_time (publish_time),
    KEY idx_is_top (is_top),
    KEY idx_is_hot (is_hot)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康资讯表';

-- ======================== 初始化数据 ========================

-- 初始化角色
INSERT INTO sys_role (id, role_code, role_name, description, sort_order, status) VALUES
(1, 'ROLE_ADMIN', '系统管理员', '系统超级管理员', 1, 1),
(2, 'ROLE_DOCTOR', '医生', '医生角色', 2, 1),
(3, 'ROLE_USER', '普通用户', '普通用户角色', 3, 1);

-- 初始化权限
INSERT INTO sys_permission (id, permission_code, permission_name, resource_type, parent_id, sort_order, path, icon, status) VALUES
(1, 'user:manage', '用户管理', 'menu', 0, 1, '/user', 'User', 1),
(2, 'user:archive', '健康档案', 'menu', 0, 2, '/archive', 'Document', 1),
(3, 'record:manage', '健康记录', 'menu', 0, 3, '/record', 'Edit', 1),
(4, 'report:manage', '体检报告', 'menu', 0, 4, '/report', 'Reading', 1),
(5, 'warning:manage', '健康预警', 'menu', 0, 5, '/warning', 'Warning', 1),
(6, 'news:manage', '健康资讯', 'menu', 0, 6, '/news', 'News', 1),
(7, 'system:manage', '系统管理', 'menu', 0, 7, '/system', 'Setting', 1),
(8, 'user:view', '查看用户', 'button', 1, 1, NULL, NULL, 1),
(9, 'user:edit', '编辑用户', 'button', 1, 2, NULL, NULL, 1),
(10, 'report:view', '查看报告', 'button', 4, 1, NULL, NULL, 1),
(11, 'report:edit', '编辑报告', 'button', 4, 2, NULL, NULL, 1),
(12, 'warning:view', '查看预警', 'button', 5, 1, NULL, NULL, 1),
(13, 'warning:handle', '处理预警', 'button', 5, 2, NULL, NULL, 1),
(14, 'news:view', '查看资讯', 'button', 6, 1, NULL, NULL, 1),
(15, 'news:edit', '编辑资讯', 'button', 6, 2, NULL, NULL, 1);

-- 初始化用户 (密码都是 BCrypt 加密的 "123456")
INSERT INTO sys_user (id, username, password, real_name, phone, email, gender, age, height, weight, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '13800000000', 'admin@health.com', 1, 28, 175.0, 70.0, 1),
(2, 'doctor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李医生', '13900000001', 'doctor@health.com', 1, 35, NULL, NULL, 1),
(3, 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', '13700000002', 'user@health.com', 1, 25, 172.0, 65.0, 1),
(4, 'user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', '13600000003', 'user2@health.com', 2, 30, 160.0, 55.0, 1);

-- 初始化用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 3);

-- 初始化角色权限关联 (管理员拥有所有权限)
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15);

-- 医生权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 8), (2, 10), (2, 11), (2, 12), (2, 13), (2, 14);

-- 普通用户权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 8), (3, 10), (3, 12), (3, 14);

-- 初始化健康档案分类
INSERT INTO health_archive_category (category_name, category_code, description, icon, sort_order, status) VALUES
('基础健康信息', 'basic_info', '基础健康信息档案', 'User', 1, 1),
('体检报告', 'exam_report', '各类体检报告档案', 'Reading', 2, 1),
('日常监测记录', 'daily_monitor', '日常健康监测记录', 'Edit', 3, 1),
('疾病诊疗记录', 'medical_record', '疾病诊疗相关记录', 'Document', 4, 1),
('用药记录', 'medication', '用药记录管理', 'Medication', 5, 1),
('运动记录', 'exercise', '运动健身记录', 'Trophy', 6, 1),
('饮食记录', 'diet', '饮食营养记录', 'Food', 7, 1);

-- 初始化体检项目
INSERT INTO exam_item (item_code, item_name, category, unit, reference_min, reference_max, is_common, sort_order, status) VALUES
('bp_systolic', '收缩压', 'blood_pressure', 'mmHg', 90.00, 140.00, 1, 1, 1),
('bp_diastolic', '舒张压', 'blood_pressure', 'mmHg', 60.00, 90.00, 1, 2, 1),
('heart_rate', '心率', 'cardiac', '次/分', 60.00, 100.00, 1, 3, 1),
('blood_sugar', '空腹血糖', 'metabolic', 'mmol/L', 3.90, 6.10, 1, 4, 1),
('total_cholesterol', '总胆固醇', 'metabolic', 'mmol/L', 0.00, 5.20, 1, 5, 1),
('ldl_cholesterol', '低密度脂蛋白', 'metabolic', 'mmol/L', 0.00, 3.40, 1, 6, 1),
('hdl_cholesterol', '高密度脂蛋白', 'metabolic', 'mmol/L', 1.00, 99.00, 1, 7, 1),
('triglyceride', '甘油三酯', 'metabolic', 'mmol/L', 0.00, 1.70, 1, 8, 1),
('hemoglobin', '血红蛋白', 'blood', 'g/L', 115.00, 150.00, 1, 9, 1),
('wbc_count', '白细胞计数', 'blood', '10^9/L', 4.00, 10.00, 1, 10, 1),
('rbc_count', '红细胞计数', 'blood', '10^12/L', 3.80, 5.50, 0, 11, 1),
('platelet_count', '血小板计数', 'blood', '10^9/L', 100.00, 300.00, 0, 12, 1),
('creatinine', '肌酐', 'kidney', 'μmol/L', 44.00, 133.00, 0, 13, 1),
('urea', '尿素氮', 'kidney', 'mmol/L', 2.90, 8.20, 0, 14, 1),
('alt', '谷丙转氨酶', 'liver', 'U/L', 0.00, 40.00, 1, 15, 1),
('ast', '谷草转氨酶', 'liver', 'U/L', 0.00, 40.00, 0, 16, 1),
('bilirubin_total', '总胆红素', 'liver', 'μmol/L', 3.40, 20.50, 0, 17, 1),
('body_mass_index', '体质指数', 'body', 'kg/m²', 18.50, 24.00, 1, 18, 1),
('waistline', '腰围', 'body', 'cm', 0.00, 90.00, 0, 19, 1),
('body_fat_rate', '体脂率', 'body', '%', 10.00, 25.00, 0, 20, 1);

-- 初始化健康资讯
INSERT INTO health_news (title, summary, content, cover_image, author, source, category, tags, view_count, is_top, is_hot, publish_time, status) VALUES
('高血压患者的日常护理指南', '高血压是常见的慢性病，合理的日常护理对控制血压至关重要...', '高血压患者的日常护理指南\n\n高血压是最常见的慢性病之一，也是心脑血管病最主要的危险因素。本文将为您介绍高血压患者的日常护理要点。\n\n1. 合理饮食：低盐低脂，多吃蔬菜水果\n2. 规律作息：保证充足睡眠，避免熬夜\n3. 适度运动：每周3-5次有氧运动\n4. 定期监测：每天测量血压，记录变化\n5. 按时服药：遵医嘱服药，不可随意停药', NULL, '健康专家', '健康科普', '慢性病', '高血压,护理,慢性病', 1280, 1, 1, '2024-01-15 09:00:00', 1),
('健康饮食的十大原则', '均衡饮食是健康的基础，掌握这十大原则让您吃出健康...', '健康饮食的十大原则\n\n1. 食物多样，谷类为主\n2. 多吃蔬菜、水果、薯类\n3. 常吃奶类、豆类及其制品\n4. 适量吃鱼、禽、蛋、瘦肉\n5. 少用油盐糖，清淡饮食\n6. 食不过量，天天运动\n7. 粗细搭配，多吃粗粮\n8. 三餐分配要合理\n9. 每天饮水，合理选择饮料\n10. 饮酒要限量', NULL, '营养师', '营养学', '饮食健康', '饮食,营养,健康', 986, 1, 1, '2024-01-20 10:00:00', 1),
('久坐办公室如何保护颈椎', '长时间低头办公容易引发颈椎问题，学会这些方法保护颈椎健康...', '久坐办公室如何保护颈椎\n\n现代上班族长时间低头工作，颈椎健康问题日益突出。以下是一些保护颈椎的实用建议：\n\n1. 保持正确坐姿：背部挺直，屏幕与视线平齐\n2. 定时活动：每1小时起身活动5-10分钟\n3. 颈部锻炼：做简单的颈部伸展运动\n4. 使用合适的办公桌椅：高度适中\n5. 注意保暖：避免空调直吹颈部\n6. 热敷放松：下班后用热水袋热敷颈部', NULL, '骨科医生', '健康科普', '运动健康', '颈椎,办公,职业病', 756, 0, 1, '2024-01-25 14:30:00', 1),
('体检报告解读：关键指标看这里', '拿到体检报告不知道怎么看？本文教你关注这些关键指标...', '体检报告解读\n\n体检报告包含大量医学指标，普通读者很难完全理解。以下是需要重点关注的指标：\n\n1. 血压：正常范围90/60-140/90mmHg\n2. 血糖：空腹正常3.9-6.1mmol/L\n3. 血脂：总胆固醇<5.2mmol/L\n4. 肝肾功能：转氨酶、肌酐等\n5. 血常规：血红蛋白、白细胞等\n6. 尿常规：蛋白尿、血尿等\n\n如指标异常，建议及时就医咨询。', NULL, '体检专家', '体检中心', '体检指南', '体检,报告,指标解读', 2156, 1, 1, '2024-02-01 08:00:00', 1),
('每天走多少步最健康', '走路是最简单的运动方式，但走多少步才科学呢？', '每天走多少步最健康\n\n关于每日步数，不同人群有不同建议：\n\n1. 健康成年人：建议每天8000-10000步\n2. 老年人：建议每天4000-6000步\n3. 办公室人群：建议每小时起身走动\n4. 糖尿病患者：餐后30分钟散步效果佳\n\n注意事项：\n- 步速适中，每分钟100-120步\n- 坚持规律，每周至少5天\n- 饭后1小时再运动', NULL, '运动专家', '运动健康', '运动健康', '走路,运动,健身', 1892, 0, 1, '2024-02-05 16:00:00', 1),
('糖尿病患者的饮食禁忌', '糖尿病患者在饮食上有诸多禁忌，了解这些才能更好控制血糖...', '糖尿病患者的饮食禁忌\n\n糖尿病患者需要特别注意饮食控制：\n\n1. 禁忌高糖食物：糖果、蛋糕、含糖饮料\n2. 限制精制碳水：白米饭、白面包、面条\n3. 多吃膳食纤维：蔬菜、全谷物、豆类\n4. 适量优质蛋白：鱼、禽、蛋、奶\n5. 烹饪方式：蒸煮优先，少煎炸\n6. 水果选择：低糖水果，如苹果、柚子\n7. 避免饮酒：酒精影响血糖代谢', NULL, '内分泌医生', '慢性病', '慢性病', '糖尿病,饮食,血糖', 1456, 0, 1, '2024-02-10 11:00:00', 1);

-- 初始化健康记录示例数据
INSERT INTO health_record (user_id, category_id, title, record_type, record_value, record_date, remark, status) VALUES
(3, 3, '血压监测记录', 'blood_pressure', '{"systolic": 125, "diastolic": 82, "pulse": 72}', '2024-03-01', '早晨测量', 1),
(3, 3, '血糖监测记录', 'blood_sugar', '{"value": 6.8, "type": "fasting"}', '2024-03-02', '空腹测量', 1),
(3, 3, '心率监测记录', 'heart_rate', '{"value": 75}', '2024-03-03', '静息状态', 1),
(3, 3, '血压监测记录', 'blood_pressure', '{"systolic": 130, "diastolic": 85, "pulse": 70}', '2024-03-04', '晚间测量', 1),
(4, 3, '血压监测记录', 'blood_pressure', '{"systolic": 135, "diastolic": 88, "pulse": 78}', '2024-03-01', '中午测量', 1),
(4, 3, '血糖监测记录', 'blood_sugar', '{"value": 7.2, "type": "postprandial"}', '2024-03-02', '餐后2小时', 0);

-- 初始化体检报告示例数据
INSERT INTO exam_report (user_id, report_no, report_title, exam_date, hospital, doctor, summary, conclusion, suggestion, status) VALUES
(3, 'REP20240301001', '2024年度健康体检报告', '2024-03-01', '市中心医院体检中心', '王医生', '各项指标基本正常，血压略高，血脂偏高。', '1. 血压偏高，建议定期监测\n2. 血脂偏高，注意饮食控制\n3. 建议加强运动锻炼', '低盐低脂饮食，适量运动，3个月后复查血脂。', 1),
(4, 'REP20240315001', '2024春季健康体检报告', '2024-03-15', '仁爱体检中心', '李医生', '整体健康状况良好，血糖轻度偏高。', '1. 空腹血糖略高，建议复查\n2. 体重超标，建议减重\n3. 各项指标基本正常', '控制饮食，减少糖分摄入，增加运动量。', 1);

-- 体检报告明细
INSERT INTO exam_report_item (report_id, item_id, item_name, item_value, unit, reference_range, is_abnormal, abnormal_level, remark) VALUES
(1, 1, '收缩压', '128', 'mmHg', '90-140', 0, NULL, NULL),
(1, 2, '舒张压', '85', 'mmHg', '60-90', 0, NULL, NULL),
(1, 3, '心率', '72', '次/分', '60-100', 0, NULL, NULL),
(1, 4, '空腹血糖', '5.8', 'mmol/L', '3.9-6.1', 0, NULL, NULL),
(1, 5, '总胆固醇', '5.8', 'mmol/L', '<5.2', 1, 'mild', '轻度偏高'),
(1, 8, '甘油三酯', '2.1', 'mmol/L', '<1.7', 1, 'mild', '轻度偏高'),
(1, 15, '谷丙转氨酶', '28', 'U/L', '0-40', 0, NULL, NULL),
(1, 18, '体质指数', '22.5', 'kg/m²', '18.5-24', 0, NULL, NULL),
(2, 1, '收缩压', '120', 'mmHg', '90-140', 0, NULL, NULL),
(2, 2, '舒张压', '78', 'mmHg', '60-90', 0, NULL, NULL),
(2, 4, '空腹血糖', '6.4', 'mmol/L', '3.9-6.1', 1, 'mild', '轻度偏高'),
(2, 5, '总胆固醇', '4.9', 'mmol/L', '<5.2', 0, NULL, NULL),
(2, 18, '体质指数', '26.2', 'kg/m²', '18.5-24', 1, 'moderate', '中度超重');

-- 初始化预警数据
INSERT INTO health_warning (user_id, record_id, report_item_id, warning_type, warning_level, warning_content, warning_value, threshold_min, threshold_max, is_handled, create_time) VALUES
(3, 2, NULL, 'blood_sugar', 'medium', '血糖值6.8mmol/L，超出正常范围3.9-6.1mmol/L', '6.8', 3.9, 6.1, 0, '2024-03-02 08:00:00'),
(3, NULL, 5, 'cholesterol', 'low', '总胆固醇5.8mmol/L，轻度偏高', '5.8', 0, 5.2, 0, '2024-03-01 10:00:00'),
(3, NULL, 8, 'triglyceride', 'medium', '甘油三酯2.1mmol/L，轻度偏高', '2.1', 0, 1.7, 0, '2024-03-01 10:00:00'),
(4, 6, NULL, 'blood_sugar', 'high', '血糖值7.2mmol/L，明显偏高', '7.2', 3.9, 6.1, 1, '2024-03-02 10:30:00'),
(4, NULL, 11, 'bmi', 'medium', '体质指数26.2，中度超重', '26.2', 18.5, 24, 0, '2024-03-15 14:00:00');

-- =============================================
-- END OF SCRIPT
-- =============================================