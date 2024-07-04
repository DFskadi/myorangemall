package com.sp.orange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sp.orange.model.Cuisine;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CuisineMapper extends BaseMapper<Cuisine> {
}
