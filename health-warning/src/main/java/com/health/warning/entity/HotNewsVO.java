package com.health.warning.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class HotNewsVO implements Serializable {
    private Long id;
    private String title;
    private String summary;
    private String coverImage;
    private String category;
    private Integer viewCount;
    private Integer isHot;
    private Date publishTime;
}
