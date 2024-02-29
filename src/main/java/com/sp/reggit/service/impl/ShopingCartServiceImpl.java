package com.sp.reggit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.reggit.entity.ShoppingCart;
import com.sp.reggit.mapper.ShoppingCartMapper;
import com.sp.reggit.service.ShopingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShopingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShopingCartService {
}
