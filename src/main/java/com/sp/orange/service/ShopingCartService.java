package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.common.R;
import com.sp.orange.entity.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ShopingCartService extends IService<ShoppingCart> {
    ShoppingCart add(ShoppingCart shoppingCart,Long userId);
    List<ShoppingCart> list(Long userId);
    String clean(Long userId);
//    @Transactional
//    R<ShoppingCart> sub(ShoppingCart shoppingCart);

}
