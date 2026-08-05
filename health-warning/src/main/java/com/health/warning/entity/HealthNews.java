package com.health.warning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("health_news")
public class HealthNews implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String author;
    private String source;
    private String category;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer isTop;
    private Integer isHot;
    private Date publishTime;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
