package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.model.ShoppingCart;

import java.util.List;

public interface ShopingCartService extends IService<ShoppingCart> {
    ShoppingCart add(ShoppingCart shoppingCart,Long userId);
    List<ShoppingCart> list(Long userId);
    String clean(Long userId);
//    @Transactional
//    R<ShoppingCart> sub(ShoppingCart shoppingCart);

}
