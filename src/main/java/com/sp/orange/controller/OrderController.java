package com.sp.orange.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.BaseContext;
import com.sp.orange.common.R;
import com.sp.orange.entity.Orders;
import com.sp.orange.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
    //提交过来的数据没用用户信息和购物车信息
    //由于用户是登录状态，随时可以通过BaseContext查询用户和购物车的信息
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("userPage")
    public R<Page> userpage(int  page,int pageSize){
        Long userId = BaseContext.getetCurrentId();

        Page pageInfo =new Page(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,userId);
        wrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo,wrapper);
        return R.success(pageInfo);
    }

    //菜品分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //创建分页构造器
        Page<Orders> pageInfo =new Page<>(page,pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //排序条件
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

        //执行查询业务
        orderService.page(pageInfo, lambdaQueryWrapper);


        return R.success(pageInfo);
    }
}

























