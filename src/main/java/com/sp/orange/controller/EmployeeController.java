package com.sp.orange.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.R;
import com.sp.orange.entity.Employee;
import com.sp.orange.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * 登录的信息是json格式，这里的request对象是为了登录成功之后将员工对象的id存到session一份
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
//        1.将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
       password = DigestUtils.md5DigestAsHex(password.getBytes());//对密码进行md5加密
//        2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());

        //这里username这个字段加了唯一约束，不能重复，所以可以用getOne方法查询唯一数据
        Employee emp = employeeService.getOne(queryWrapper);
//        3.如果没有查询到则返回登录失败结果

        if(emp == null)
        {
            return R.error("登录失败");
        }
//        4.密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
//        5.查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

//        6.登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     * @param request
     * @return
     */
    @RequestMapping("/logout")
    public  R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
//        log.info("新增员工，员工信息：{}",employee.toString());

        byte[] bytes = "123456".getBytes();
        String password = DigestUtils.md5DigestAsHex(bytes);
        employee.setPassword(password);//需要md5加密


        //employee.setCreateTime(LocalDateTime.now());//设置记录创建时间
      //  employee.setUpdateTime(LocalDateTime.now());//设置记录更新时间

//        //设置创建的用户
     //   Long empId=(Long) request.getSession().getAttribute("employee");

       // employee.setCreateUser(empId);//获取session中的当前登录的用户id
        //employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={}",page,pageSize);

        //构造分页构造器
        Page pageInfo =new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    @PutMapping
    public R<String> upadte(HttpServletRequest request,@RequestBody Employee employee){
//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee !=null){
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }

}
















