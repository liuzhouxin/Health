package com.health.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.user.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
