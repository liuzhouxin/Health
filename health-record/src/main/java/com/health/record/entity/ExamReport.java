package com.health.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("exam_report")
public class ExamReport implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String reportNo;
    private String reportTitle;
    private Date examDate;
    private String hospital;
    private String doctor;
    private String summary;
    private String conclusion;
    private String suggestion;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
