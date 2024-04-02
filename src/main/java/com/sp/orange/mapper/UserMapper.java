package com.sp.orange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sp.orange.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
