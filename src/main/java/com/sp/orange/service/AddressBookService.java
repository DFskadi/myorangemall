package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.entity.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    AddressBook setDefault(AddressBook addressBook,Long userId);

    AddressBook getDefault();

    List<AddressBook> list(AddressBook addressBook);

    String delete(Long addrId,Long userId);
}
