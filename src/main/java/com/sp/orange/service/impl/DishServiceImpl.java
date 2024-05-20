package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sp.orange.common.CustomException;
import com.sp.orange.common.R;
import com.sp.orange.dto.DishDto;
import com.sp.orange.dto.SetmealDto;
import com.sp.orange.entity.*;
import com.sp.orange.mapper.DishMapper;
import com.sp.orange.service.CategoryService;
import com.sp.orange.service.DishFlavorService;
import com.sp.orange.service.DishService;
import com.sp.orange.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<DishDto> page(int page, int pageSize, String name) {
        //创建分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //创建条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //过滤条件
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        //执行查询业务
        dishService.page(pageInfo, lambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");//pageInfo作为源拷贝到dishDtoPage，第三个参数是忽略的属性
        //处理records
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {//这里item代表Dish，这里代码是为了设置上面对象拷贝忽略掉的recodes
            // ，这里不直接拷贝上面的recodes是因为第一个pageIfo中的泛型是Dish,而我们需要的泛型是DishDto，因此需要自己设置
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//通过分类对象拿到每个菜品的分类id
            //这里需要使用categoryId去查询分类表，拿到想要的数据，所以需要注入分类Service
            String categoryName = categoryService.getById(categoryId).getName();
            //将查询出来的categoryName赋值给DishDTO
            if (categoryName != null) {
                dishDto.setCategoryName(categoryName);//这里要有非空判断，教程中测试，由于根据查不到数据库中中分类名字段而报空指针异常
            }
            //dishDto是 new出来的，如果仅仅set其中一个属性，那么它继承的对象Dish中其它的属性为空，所以这里同样需要对象拷贝
            //这里展示的数据来自新创建的dishDto和它继承Dish，因此需要第二次的对象拷贝

            //返回dishDto对象
            return dishDto;
        }).collect(Collectors.toList());//最终赋值给下面创建的集合
        //List<DishDto> list =null;
        Page<DishDto> dishDtoPages = dishDtoPage.setRecords(list);
        return dishDtoPages;
    }

    /**
     * 根据条件查询对应的菜品数据
     * 这里集合内的泛型数据改为DishDto，以便页面能够既展示菜品的基本信息，又展示菜品的口味信息
     *
     * @param dish
     * @return
     */
    @Override
    public List<DishDto> list(Dish dish) {//这里参数为什么不是 Long categoryId，是为了让此方法的通用性更强，传过来其它字段也可以复用
        List<DishDto> dishDtoList = null;
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);//添加条件，查询状态为1(起售)
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return dishDtoList;
    }


    /**
     * 新增菜品，同时保存对应的口味数据
     *
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
        flavors = flavors.stream().map((item) -> {

            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 根据id查询菜品信息和口味信息
     * 回显菜品信息
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
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        Long dishId=dishDto.getId();
        log.info(dishId.toString());
        //更新dish表基本信息
        this.updateById(dishDto);
        //清理当前菜品对应口味数据（菜品口味之前是3个维度，现在重新设置有可能删除一个维度，因此需要清空之前表中的数据）
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dishId!=null,DishFlavor::getDishId,dishId);//dishDto类中并没有id属性，这个id是他的父类Dish的属性
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据
        //和新增时情况相同dishId没有被封装，需要手动封装
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品前要保证菜品不在套餐中且菜品为停售状态
     * @param ids
     */
    @Override
    @Transactional//多张表操作，需要开启事务
    public void deleteWithFlavor(List<Long> ids) {
        //查询菜品状态，确定是否可以删除
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ids != null, Dish::getId, ids);
        lambdaQueryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(lambdaQueryWrapper);

        //查询菜品是否存在于套餐中
        LambdaQueryWrapper<SetmealDish> squeryWrapper = new LambdaQueryWrapper<>();
        squeryWrapper.in(ids != null, SetmealDish::getDishId, ids);
        int flag = setmealDishService.count(squeryWrapper);

        //如果不能删除抛出一个业务异常(自定义的异常)
        if (flag > 0) {
            throw new CustomException("菜品位于套餐中，不能删除");
        }

        //如果不能删除抛出一个业务异常(自定义的异常)
        if (count > 0) {
            throw new CustomException("菜品正在售卖中，不能删除");
        }


        //逻辑删除当前菜品
        LambdaUpdateWrapper<Dish> queryWrapper = new LambdaUpdateWrapper();
        queryWrapper.in(ids != null, Dish::getId, ids).set(Dish::getIsDeleted, 1);
        dishService.update(queryWrapper);
//        //删除当前菜品对应口味数据
//        LambdaUpdateWrapper<DishFlavor> updateWrapper = new LambdaUpdateWrapper();
//        updateWrapper.in(ids != null, DishFlavor::getDishId, ids).set(DishFlavor::getIsDeleted, 1);
        LambdaQueryWrapper<DishFlavor> dishflavorWrapper =new LambdaQueryWrapper<>();
        dishflavorWrapper.in(ids != null, DishFlavor::getDishId, ids);
        dishFlavorService.remove(dishflavorWrapper);
    }


    /**
     * 菜品停售&起售
     */
    @Override
    public List<Dish> status(Integer status,List<Long> ids){

        //查询菜品是否存在于套餐中
        LambdaQueryWrapper<SetmealDish> squeryWrapper =new LambdaQueryWrapper<>();
        squeryWrapper.in(ids!=null,SetmealDish::getDishId,ids);
        int flag = setmealDishService.count(squeryWrapper);
        //如果不能删除抛出一个业务异常(自定义的异常)
        if(flag>0){
            throw new CustomException("菜品位于套餐中，不能停售");
        }

        LambdaUpdateWrapper<Dish> updateWrapper =new LambdaUpdateWrapper<>();
        updateWrapper.in(ids !=null,Dish::getId,ids).set(Dish::getStatus,status);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> dishList =this.list(lambdaQueryWrapper);
        this.update(updateWrapper);
        return dishList;
    }

}










