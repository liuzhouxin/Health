package com.health.warning.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class WarningCountVO implements Serializable {
    private Long total;
    private Integer criticalCount;
    private Integer highCount;
    private Integer mediumCount;
    private Integer lowCount;
    private Integer unhandledCount;
}
