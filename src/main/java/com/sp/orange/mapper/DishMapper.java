package com.sp.orange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sp.orange.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
