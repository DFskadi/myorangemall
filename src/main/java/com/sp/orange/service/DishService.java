package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.dto.DishDto;
import com.sp.orange.entity.Dish;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    @Transactional
    //多张表操作，需要开启事务
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和口味信息
    DishDto getByIdWithFlavor(Long id);

    //修改菜品，同时修改菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    @Transactional
    //多张表操作，需要开启事务
    void updateWithFlavor(DishDto dishDto);

    @Transactional
    //多张表操作，需要开启事务
    void deleteWithFlavor(List<Long> ids);

    List<Dish> status(Integer status, List<Long> ids);

    Page<DishDto> page(int page, int pageSize, String name);

    List<DishDto> list(Dish dish);
}













