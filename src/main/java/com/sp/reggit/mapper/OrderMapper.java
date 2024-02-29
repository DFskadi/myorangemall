package com.sp.reggit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sp.reggit.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
