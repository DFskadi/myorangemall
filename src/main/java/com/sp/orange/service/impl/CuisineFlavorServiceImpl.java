package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.model.CuisineFlavor;
import com.sp.orange.mapper.DishFlavorMapper;
import com.sp.orange.service.DishFlavorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuisineFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, CuisineFlavor> implements DishFlavorService {
}
