package com.sp.orange.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sp.orange.common.BaseContext;
import com.sp.orange.common.CustomException;
import com.sp.orange.common.R;
import com.sp.orange.model.ShoppingCart;
import com.sp.orange.service.ShopingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart,HttpServletRequest request) {
        Long userId =(Long) request.getSession().getAttribute("user");
        ShoppingCart cart = shopingCartService.add(shoppingCart,userId);
        return R.success(cart);
    }


    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpServletRequest request){
        Long userId =(Long) request.getSession().getAttribute("user");
        List<ShoppingCart> list = shopingCartService.list(userId);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(HttpServletRequest request){
        Long userId =(Long) request.getSession().getAttribute("user");
        String str = shopingCartService.clean(userId);
        return R.success(str);
    }


    /**
     * 购物车增减少
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    @Transactional
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> dqueryWrapper=new LambdaQueryWrapper<>();
        //代表数量减少的是菜品数量
        if(dishId!=null){
            //通过dishId查出购物车对象
            //这里必须要加两个条件，否则会出现用户互相修改对方与自己购物车中相同套餐或者是菜品的数量
            dqueryWrapper.eq(ShoppingCart::getDishId,dishId)
                    .eq(ShoppingCart::getUserId,BaseContext.getetCurrentId())
            ;
            ShoppingCart dcart = shopingCartService.getOne(dqueryWrapper);
            dcart.setNumber(dcart.getNumber()-1);

            Integer dlatestNumber = dcart.getNumber();

            if(dlatestNumber > 0) {
                //对数据进行更新操作
                shopingCartService.updateById(dcart);
            }else if(dlatestNumber == 0){
                //如果购物车的菜品数量减为0，那么就把菜品从购物车中删除
                shopingCartService.removeById(dcart.getId());
            }else if(dlatestNumber < 0){
                throw new CustomException("操作异常");
            }
            return R.success(dcart);
        }


        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> squeryWrapper =new LambdaQueryWrapper<>();
        if(setmealId!=null){
            squeryWrapper.eq(ShoppingCart::getSetmealId,setmealId)
                    .eq(ShoppingCart::getUserId,BaseContext.getetCurrentId());
            ShoppingCart scart = shopingCartService.getOne(squeryWrapper);
            scart.setNumber(scart.getNumber()-1);
            Integer slatestNumber = scart.getNumber();
            if(slatestNumber > 0) {
                //对数据进行更新操作
                shopingCartService.updateById(scart);
            }else if(slatestNumber == 0){
                //如果购物车的菜品数量减为0，那么就把菜品从购物车中删除
                shopingCartService.removeById(scart.getId());
            }else if(slatestNumber < 0){
                throw new CustomException("操作异常");
            }

            return R.success(scart);
        }
        return R.error("操作异常");
    }
}












