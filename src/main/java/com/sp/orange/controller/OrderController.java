package com.sp.orange.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.R;
import com.sp.orange.model.Orders;
import com.sp.orange.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders,HttpServletRequest request) {
        Long userId =(Long) request.getSession().getAttribute("user");
        //提交过来的数据没用用户信息和购物车信息
        //由于用户是登录状态，随时可以通过BaseContext查询用户和购物车的信息
        orderService.submit(orders,userId);
        return R.success("下单成功");
    }

    /**
     * 前端用户浏览订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("userPage")
    public R<Page> userpage(int page, int pageSize, HttpServletRequest request) {
        Long userId =(Long) request.getSession().getAttribute("user");
        Page pageInfo = orderService.userPage(page, pageSize,userId);
        return R.success(pageInfo);
    }

    /**
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page pageInfo = orderService.page(page, pageSize, number, beginTime, endTime);
        return R.success(pageInfo);
    }

    @PutMapping
    public R status(@RequestBody Map map){
        Long id = Long.parseLong((String)map.get("id"));
        String str = orderService.status(id);
        return R.success(str);
    }




    /**
     * 户端点击再来一单
     * @param map
     * @param request
     * @return
     */
    @PostMapping("/again")
    public R<String> againSubmit(@RequestBody Map<String,String> map,HttpServletRequest request){
        Long userId =(Long) request.getSession().getAttribute("user");
        Long orderId = Long.parseLong(map.get("id"));
        String str = orderService.againSubmit(orderId, userId);
        return R.success(str);
    }


}

























