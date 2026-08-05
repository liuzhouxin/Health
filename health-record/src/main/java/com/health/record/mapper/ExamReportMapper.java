package com.health.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.record.entity.ExamReport;
import com.health.record.entity.ExamReportListItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamReportMapper extends BaseMapper<ExamReport> {

    List<ExamReportListItem> selectReportListWithAbnormalCount(@Param("userId") Long userId);
}
