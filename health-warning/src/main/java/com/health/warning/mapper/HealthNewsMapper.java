package com.health.warning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.warning.entity.HealthNews;
import com.health.warning.entity.HotNewsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HealthNewsMapper extends BaseMapper<HealthNews> {

    List<HotNewsVO> selectHotNews(@Param("limit") int limit);

    List<HealthNews> selectNewsByCategory(@Param("category") String category,
                                          @Param("limit") int limit);
}
