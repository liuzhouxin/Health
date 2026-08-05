package com.health.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.record.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {

    @Select("SELECT * FROM health_record WHERE user_id = #{userId} " +
            "AND record_type = #{recordType} ORDER BY record_date DESC LIMIT #{limit}")
    List<HealthRecord> selectByUserAndType(@Param("userId") Long userId,
                                           @Param("recordType") String recordType,
                                           @Param("limit") int limit);
}
