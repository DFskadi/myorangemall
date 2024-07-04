package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.model.AddressBook;
import com.sp.orange.mapper.AddressBookMapper;
import com.sp.orange.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @Override
    public AddressBook setDefault(AddressBook addressBook,Long userId) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,userId);
        wrapper.set(AddressBook::getIsDefault, 0);
        //默认地址只能有一个，先将用户设置的所有地址信息is_default字段置为0
        //SQL:update address_book set is_default =0 where user_id=?
        this.update(wrapper);
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default =1 where user_id=?
        this.updateById(addressBook);
        return addressBook;
    }

    /**
     * 查询用户默认地址
     *
     * @return
     */
    @Override
    public AddressBook getDefault(Long userId) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        //SQL:select * from address_book where user_id=? and   is_default =1
        AddressBook addressBook = this.getOne(queryWrapper);
        return addressBook;
    }

    /**
     * 查询指定用户的全部地址信息
     *
     * @param addressBook
     * @return
     */
    @Override
    public List<AddressBook> list(AddressBook addressBook,Long userId) {
        addressBook.setUserId(userId);
        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != userId, AddressBook::getUserId, userId);
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id =? order by update_time desc
        return this.list(queryWrapper);
    }


    @Override
    public String delete(Long addrId,Long userId){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getId,addrId)
                .eq(AddressBook::getUserId,userId);
        this.remove(lambdaQueryWrapper);
        return "删除地址成功";
    }


}
