package com.health.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.record.entity.ExamReportItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamReportItemMapper extends BaseMapper<ExamReportItem> {

    List<ExamReportItem> selectByReportId(@Param("reportId") Long reportId);
}
