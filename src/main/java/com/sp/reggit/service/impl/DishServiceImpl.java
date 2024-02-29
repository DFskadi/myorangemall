package com.sp.reggit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sp.reggit.dto.DishDto;
import com.sp.reggit.entity.Dish;
import com.sp.reggit.entity.DishFlavor;
import com.sp.reggit.mapper.DishMapper;
import com.sp.reggit.service.DishFlavorService;
import com.sp.reggit.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional//多张表操作，需要开启事务
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        this.save(dishDto);//dishDto继承dish，直接保存它即可
        Long dishId = dishDto.getId();
        //保存菜品口味数据到菜品口味表dish_flavor
       // dishFlavorService.saveBatch(dishDto.getFlavors());//因为保存的是一个集合，所以使用批量保存
        //通过前端传来的数据看到 getFlavors中仅仅封装了name 和 value 没有对id进行封装

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //处理集合，为dish_id赋值，这里既可以使用for循环也可以使用stream流
        flavors = flavors.stream().map((item)->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto=new DishDto();

        //1.查询菜品基本信息，从dish表中查
        Dish dish = this.getById(id);
        //2.查询当前菜品对应的口味信息，从dish_flavor中查
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        //返回Dto对象，需要进行拷贝
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据（菜品口味之前是3个维度，现在重新设置有可能删除一个维度，因此需要清空之前表中的数据）
        LambdaQueryWrapper<DishFlavor> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());//dishDto类中并没有id属性，这个id是他的父类Dish的属性
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据
        //和新增时情况相同dishId没有被封装，需要手动封装
        List<DishFlavor> flavors =dishDto.getFlavors();
        flavors  = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}










