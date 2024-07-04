package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.model.ShoppingCart;
import com.sp.orange.mapper.ShoppingCartMapper;
import com.sp.orange.service.ShopingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ShopingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShopingCartService {
    /**
     * 添加到购物车
     *
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart,Long userId) {

        //设置用户id，指定是哪个用户的购物车数据
        shoppingCart.setUserId(userId);
        //由于用户可能操作是：点开添加购物车页面，添加，再次点开添加购物车页面，添加
        //上述操作，购物车中number属性+1，而不是存储两条数据
        //所以需要判断购物车中是否存在当前菜品信息，如果存在，添加到购物车后number+1,如果不存在number默认是1
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);


        if (dishId != null) {
            //说明添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //SQL:select * from shopping_cart where user_id =? and dish_id =?
        //SQL:select * from shopping_cart where user_id =? and setmeal_id =?
        ShoppingCart cart = this.getOne(queryWrapper);

        if (cart != null) {
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            this.updateById(cart);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            cart = shoppingCart;
        }


        return cart;
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list(Long userId){
        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = this.list(queryWrapper);
        return list;
    }

    /**
     * 清空购物车
     * @return
     */
    @Override
    public String clean(Long userId){
        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        this.remove(queryWrapper);

        return "清空购物车成功";

    }

//    /**
//     * 购物车增减少
//     * @param shoppingCart
//     * @return
//     */
//    @Override
//    @Transactional
//    public R<ShoppingCart> sub(ShoppingCart shoppingCart){
//        Long dishId = shoppingCart.getDishId();
//        LambdaQueryWrapper<ShoppingCart> dqueryWrapper=new LambdaQueryWrapper<>();
//        //代表数量减少的是菜品数量
//        if(dishId!=null){
//            //通过dishId查出购物车对象
//            //这里必须要加两个条件，否则会出现用户互相修改对方与自己购物车中相同套餐或者是菜品的数量
//            dqueryWrapper.eq(ShoppingCart::getDishId,dishId)
//                    .eq(ShoppingCart::getUserId,BaseContext.getetCurrentId())
//            ;
//            ShoppingCart dcart = this.getOne(dqueryWrapper);
//            dcart.setNumber(dcart.getNumber()-1);
//
//            Integer dlatestNumber = dcart.getNumber();
//
//            if(dlatestNumber > 0) {
//                //对数据进行更新操作
//                this.updateById(dcart);
//            }else if(dlatestNumber == 0){
//                //如果购物车的菜品数量减为0，那么就把菜品从购物车中删除
//                this.removeById(dcart.getId());
//            }else if(dlatestNumber < 0){
//                throw new CustomException("操作异常");
//            }
//            return R.success(dcart);
//        }
//
//
//        Long setmealId = shoppingCart.getSetmealId();
//        LambdaQueryWrapper<ShoppingCart> squeryWrapper =new LambdaQueryWrapper<>();
//        if(setmealId!=null){
//            squeryWrapper.eq(ShoppingCart::getSetmealId,setmealId)
//                    .eq(ShoppingCart::getUserId,BaseContext.getetCurrentId());
//            ShoppingCart scart = this.getOne(squeryWrapper);
//            scart.setNumber(scart.getNumber()-1);
//            Integer slatestNumber = scart.getNumber();
//            if(slatestNumber > 0) {
//                //对数据进行更新操作
//                this.updateById(scart);
//            }else if(slatestNumber == 0){
//                //如果购物车的菜品数量减为0，那么就把菜品从购物车中删除
//                this.removeById(scart.getId());
//            }else if(slatestNumber < 0){
//                throw new CustomException("操作异常");
//            }
//
//            return R.success(scart);
//        }
//        return R.error("操作异常");
//    }



}
