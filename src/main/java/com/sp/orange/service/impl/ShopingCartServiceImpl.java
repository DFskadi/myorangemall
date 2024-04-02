package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.entity.ShoppingCart;
import com.sp.orange.mapper.ShoppingCartMapper;
import com.sp.orange.service.ShopingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShopingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShopingCartService {
}
