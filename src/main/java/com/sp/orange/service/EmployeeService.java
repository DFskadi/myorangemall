package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.common.R;
import com.sp.orange.entity.Employee;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {
    //@Transactional
    //R<Employee> login(HttpServletRequest request,Employee employee);

    Page page(int page, int pageSize, String name);
}
