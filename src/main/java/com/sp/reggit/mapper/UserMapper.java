package com.sp.reggit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sp.reggit.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
