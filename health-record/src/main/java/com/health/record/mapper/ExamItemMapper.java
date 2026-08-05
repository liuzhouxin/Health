package com.health.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.record.entity.ExamItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamItemMapper extends BaseMapper<ExamItem> {

    List<ExamItem> selectCommonItems();
}
