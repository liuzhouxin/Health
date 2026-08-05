package com.health.warning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.warning.entity.HealthWarning;
import com.health.warning.entity.WarningCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HealthWarningMapper extends BaseMapper<HealthWarning> {

    WarningCountVO selectWarningCountByUserId(@Param("userId") Long userId);

    List<HealthWarning> selectWarningsByUserId(@Param("userId") Long userId,
                                              @Param("warningLevel") String warningLevel,
                                              @Param("isHandled") Integer isHandled);
}
