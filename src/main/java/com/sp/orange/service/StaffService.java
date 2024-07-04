package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.model.Staff;

public interface StaffService extends IService<Staff> {
    //@Transactional
    //R<Employee> login(HttpServletRequest request,Employee employee);

    Page page(int page, int pageSize, String name);

//    String upadte(Employee employee);
}
