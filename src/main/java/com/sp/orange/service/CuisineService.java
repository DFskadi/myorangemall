package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.dto.CuisineDto;
import com.sp.orange.model.Cuisine;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface CuisineService extends IService<Cuisine> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    @Transactional
    //多张表操作，需要开启事务
    void saveWithFlavor(CuisineDto dishDto);

    //根据id查询菜品信息和口味信息
    CuisineDto getByIdWithFlavor(Long id);

    //修改菜品，同时修改菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    @Transactional
    //多张表操作，需要开启事务
    void updateWithFlavor(CuisineDto dishDto);

    @Transactional
    //多张表操作，需要开启事务
    void deleteWithFlavor(List<Long> ids);

    List<Cuisine> status(Integer status, List<Long> ids);

    Page<CuisineDto> page(int page, int pageSize, String name);

    List<CuisineDto> list(Cuisine cuisine);
}













