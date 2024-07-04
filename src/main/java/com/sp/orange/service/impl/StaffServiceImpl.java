package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.model.Staff;
import com.sp.orange.mapper.StaffMapper;
import com.sp.orange.service.StaffService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {

//    /**
//     * 员工登录
//     * 登录的信息是json格式，这里的request对象是为了登录成功之后将员工对象的id存到session一份
//     *
//     * @param employee
//     * @return
//     */
//    @Override
//    public R<Employee> login(HttpServletRequest request,Employee employee) {
////        1.将页面提交的密码password进行md5加密处理
//        String password = employee.getPassword();
//        password = DigestUtils.md5DigestAsHex(password.getBytes());//对密码进行md5加密
////       2.根据页面提交的用户名username查询数据库
//        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Employee::getUsername, employee.getUsername());
//
//        //这里username这个字段加了唯一约束，不能重复，所以可以用getOne方法查询唯一数据
//        Employee emp = this.getOne(queryWrapper);
//        //       如果没有查询到则返回登录失败结果
//        if (emp == null) {
//            return R.error("登录失败");
//        }
////        4.密码比对，如果不一致则返回登录失败结果
//        if (!emp.getPassword().equals(password)) {
//            return R.error("密码错误");
//        }
////        5.查看员工状态，如果为已禁用状态，则返回员工已禁用结果
//        if (emp.getStatus() == 0) {
//            return R.error("账号已禁用");
//        }
//        request.getSession().setAttribute("employee", emp.getId());
//
//        return R.success(emp);
//    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page page(int page, int pageSize, String name) {
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Staff::getName, name);
        //添加一个排序条件
        queryWrapper.orderByDesc(Staff::getUpdateTime);
        //执行查询
        this.page(pageInfo, queryWrapper);
        return pageInfo;
    }

//    @Override
//    public String upadte(Employee employee) {
//        LambdaUpdateWrapper<Employee> queryWrapper=new LambdaUpdateWrapper<>();
//        queryWrapper.ne(employee.getId()!=null,Employee::getId,1);
//        boolean flag = this.updateById(employee);
//        if (flag){
//            return "员工信息修改成功";
//        }
//        return "您没有此权限请联系管理员";
//    }

}
