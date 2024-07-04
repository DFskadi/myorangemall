package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.model.ComboCuisine;
import com.sp.orange.mapper.SetmealDishMapper;
import com.sp.orange.service.ComboCuisineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ComboCuisineServiceImpl extends ServiceImpl<SetmealDishMapper, ComboCuisine> implements ComboCuisineService {
}
