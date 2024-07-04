package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.model.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    AddressBook setDefault(AddressBook addressBook,Long userId);

    AddressBook getDefault(Long userId);

    List<AddressBook> list(AddressBook addressBook,Long userId);

    String delete(Long addrId,Long userId);
}
