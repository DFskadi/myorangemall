package com.sp.orange.controller;

import com.sp.orange.common.R;
import com.sp.orange.model.AddressBook;
import com.sp.orange.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 用户新增地址
     * @param addressBook
     * @param request
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook,HttpServletRequest request){
        Long userId =(Long) request.getSession().getAttribute("user");
        addressBook.setUserId(userId);
//        addressBook.setCreateTime(LocalDateTime.now());
//        addressBook.setCreateUser(userId);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook,HttpServletRequest request){
        Long userId =(Long) request.getSession().getAttribute("user");
        AddressBook addBook = addressBookService.setDefault(addressBook,userId);
        return R.success(addBook);
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
                  return R.error("没有查询到地址");
              }
        }

    /**
     * 查询用户默认地址
     * @return
     */
        @GetMapping("default")
        public R<AddressBook> getDefault(HttpServletRequest request){
            Long userId =(Long) request.getSession().getAttribute("user");
            AddressBook addressBook = addressBookService.getDefault(userId);
            if(null ==addressBook){
                return R.error("没有查询到地址");
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
    public R<List<AddressBook>> list(AddressBook addressBook,HttpServletRequest request){
        Long userId =(Long) request.getSession().getAttribute("user");
        List<AddressBook> list = addressBookService.list(addressBook,userId);
        return R.success(list);
    }

    /**
     * 删除地址
     *
     * @param addrId
     * @param request
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids")Long addrId,HttpServletRequest request){
        Long userId =(Long) request.getSession().getAttribute("user");
        if(addrId==null){
            return R.error("请求异常");
        }
        String str = addressBookService.delete(addrId, userId);
        return R.success(str);
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        if (addressBook==null){
            return R.error("请求异常");
        }
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }


}

























