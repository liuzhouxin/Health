package com.health.record.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("exam_report")//体检报告
public class ExamReport implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String reportNo;//报告编号
    private String reportTitle;//报告标题
    private Date examDate;//检测日期
    private String hospital;//医院
    private String doctor;//医生
    private String summary;//总结
    private String conclusion;//结论
    private String suggestion;//建议
    private Integer status;//状态
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
