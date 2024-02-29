package com.sp.reggit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sp.reggit.common.BaseContext;
import com.sp.reggit.common.R;
import com.sp.reggit.entity.ShoppingCart;
import com.sp.reggit.service.ShopingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShopingCartService shopingCartService;


    /**
     * 添加到购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {

        //设置用户id，指定是哪个用户的购物车数据
        Long currentId = BaseContext.getetCurrentId();
        shoppingCart.setUserId(currentId);

        //由于用户可能操作是：点开添加购物车页面，添加，再次点开添加购物车页面，添加
        //上述操作，购物车中number属性+1，而不是存储两条数据
        //所以需要判断购物车中是否存在当前菜品信息，如果存在，添加到购物车后number+1,如果不存在number默认是1
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);


        if (dishId != null) {
            //说明添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //SQL:select * from shopping_cart where user_id =? and dish_id =?
        //SQL:select * from shopping_cart where user_id =? and setmeal_id =?
        ShoppingCart cart = shopingCartService.getOne(queryWrapper);

        if (cart != null) {
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            shopingCartService.updateById(cart);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shopingCartService.save(shoppingCart);
            cart = shoppingCart;
        }


        return R.success(cart);
    }


    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){

        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getetCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shopingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getetCurrentId());

        shopingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");

    }

}












