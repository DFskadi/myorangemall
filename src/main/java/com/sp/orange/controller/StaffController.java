package com.sp.orange.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.R;
import com.sp.orange.model.Staff;
import com.sp.orange.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/staff")
public class StaffController {
    @Autowired
    private StaffService StaffService;


    /**
     * 用户登录
     * @param request
     * @param staff
     * @return
     */
    @PostMapping("/login")
    public R<Staff> login(HttpServletRequest request, @RequestBody Staff staff) {
        //        1.将页面提交的密码password进行md5加密处理
        String password = staff.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());//对密码进行md5加密
//       2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Staff::getUsername, staff.getUsername());

        //这里username这个字段加了唯一约束，不能重复，所以可以用getOne方法查询唯一数据
        Staff emp = StaffService.getOne(queryWrapper);
        //       如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }
//        4.密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }
//        5.查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        request.getSession().setAttribute("staff", emp.getId());

        return R.success(emp);
    }



    @RequestMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("staff");
        return R.success("退出成功");
    }


    /**
     * 用户注册
     * @param request
     * @param staff
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Staff staff) {
//        log.info("新增员工，员工信息：{}",Employee.toString());

        byte[] bytes = "123456".getBytes();
        String password = DigestUtils.md5DigestAsHex(bytes);
        staff.setPassword(password);//需要md5加密

        //以下字段使用公共字段自动填充功能
        //Employee.setCreateTime(LocalDateTime.now());//设置记录创建时间
        //  Employee.setUpdateTime(LocalDateTime.now());//设置记录更新时间
//        //设置创建的用户
//           Long empId=(Long) request.getSession().getAttribute("Employee");
//         Employee.setCreateUser(empId);//获取session中的当前登录的用户id
//        Employee.setUpdateUser(empId);

        StaffService.save(staff);
        return R.success("新增员工成功");
    }


    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page pageInfo = StaffService.page(page, pageSize, name);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> upadte(@RequestBody Staff Staff) {
        //HttpServletRequest request之前是有这个参数的，为的是从session中取id，现在从线程中取id
//        Long empId = (Long)request.getSession().getAttribute("Employee");
//        Employee.setUpdateTime(LocalDateTime.now());
//        Employee.setUpdateUser(empId);
       StaffService.updateById(Staff);
        return R.success("修改员工信息成功");
    }

    @GetMapping("/{id}")
    public R<Staff> getById(@PathVariable Long id) {
        Staff staff = StaffService.getById(id);
        if (staff != null) {
            return R.success(staff);
        }
        return R.error("没有查询到员工信息");
    }

}
















