package com.sp.orange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sp.orange.model.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
