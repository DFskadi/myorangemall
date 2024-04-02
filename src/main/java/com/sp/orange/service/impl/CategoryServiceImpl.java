package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.common.CustomException;
import com.sp.orange.entity.Category;
import com.sp.orange.entity.Dish;
import com.sp.orange.entity.Setmeal;
import com.sp.orange.mapper.CategoryMapper;
import com.sp.orange.service.CategoryService;
import com.sp.orange.service.DishService;
import com.sp.orange.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class  CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类,删除之前需要进行判断
     * @param id
     */
    @Override
    public void removeByDS(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        //查询当前的分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (count1 > 0){//如果根据dish中分类id查询的数据存在
            throw  new CustomException("当前分类下关联了该菜品，不能删除");
        }
        //查询当前的分类是否关联了套餐，如果已经关联，抛出一个业务异常
        if(count2 > 0){
            throw  new CustomException("当前套餐下关联了该套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }
}











