package com.sp.reggit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.sp.reggit.common.BaseContext;
import com.sp.reggit.common.R;
import com.sp.reggit.entity.AddressBook;
import com.sp.reggit.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getetCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> wrapper =new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getetCurrentId());
        wrapper.set(AddressBook::getIsDefault,0);
        //默认地址只能有一个，先将用户设置的所有地址信息is_default字段置为0
        //SQL:update address_book set is_default =0 where user_id=?
        addressBookService.update(wrapper);
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default =1 where user_id=?
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }


    /**
     * 根据id查询地址
     * @param id
     * @return
     */
        @GetMapping("/{id}")
        public R get(@PathVariable Long id){
              AddressBook addressBook = addressBookService.getById(id);
              if(addressBook != null){
                  return R.success(addressBook);
              }else {
                  return R.error("没有找到该对象");
              }
        }

    /**
     * 查询用户默认地址
     * @return
     */
        @GetMapping("default")
        public R<AddressBook> getDefault(){
            LambdaQueryWrapper <AddressBook> queryWrapper =new LambdaQueryWrapper<>();
            queryWrapper.eq(AddressBook::getUserId,BaseContext.getetCurrentId());
            queryWrapper.eq(AddressBook::getIsDefault,1);
            //SQL:select * from address_book where user_id=? and   is_default =1
            AddressBook addressBook =addressBookService.getOne(queryWrapper);

            if(null ==addressBook){
                return R.error("没有找到对象");
            }else {
                return R.success(addressBook);
            }
        }


    /**
     * 查询指定用户的全部地址信息
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
            addressBook.setUserId(BaseContext.getetCurrentId());

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=addressBook.getUserId(),AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id =? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }



}

























