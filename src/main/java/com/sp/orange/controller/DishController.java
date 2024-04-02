package com.sp.orange.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.R;
import com.sp.orange.dto.DishDto;
import com.sp.orange.entity.Category;
import com.sp.orange.entity.Dish;
import com.sp.orange.entity.DishFlavor;
import com.sp.orange.service.CategoryService;
import com.sp.orange.service.DishFlavorService;
import com.sp.orange.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){//前端通过json传递给服务端数据
        dishService.saveWithFlavor(dishDto);
        //清理所有菜品的缓存
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        //精确清理
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }


   //菜品分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //创建分页构造器
        Page<Dish> pageInfo =new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage =new Page<>();
        //创建条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //过滤条件
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
        //执行查询业务
        dishService.page(pageInfo, lambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");//pageInfo作为源拷贝到dishDtoPage，第三个参数是忽略的属性
        //处理records
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{//这里item代表Dish，这里代码是为了设置上面对象拷贝忽略掉的recodes
            // ，这里不直接拷贝上面的recodes是因为第一个pageIfo中的泛型是Dish,而我们需要的泛型是DishDto，因此需要自己设置
            DishDto dishDto =new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//通过分类对象拿到每个菜品的分类id
            //这里需要使用categoryId去查询分类表，拿到想要的数据，所以需要注入分类Service
            String categoryName = categoryService.getById(categoryId).getName();
            //将查询出来的categoryName赋值给DishDTO
            if(categoryName!=null){
                dishDto.setCategoryName(categoryName);//这里要有非空判断，教程中测试，由于根据查不到数据库中中分类名字段而报空指针异常
            }
            //dishDto是 new出来的，如果仅仅set其中一个属性，那么它继承的对象Dish中其它的属性为空，所以这里同样需要对象拷贝
            //这里展示的数据来自新创建的dishDto和它继承Dish，因此需要第二次的对象拷贝

            //返回dishDto对象
            return dishDto;
        }).collect(Collectors.toList());//最终赋值给下面创建的集合
        //List<DishDto> list =null;
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){//这里使用DishDto作为泛型的原因是由于，回显的数据除了dish中的字段，同样包含口味等dish中不存在的字段
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
     dishService.updateWithFlavor(dishDto);
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
      return R.success("修改菜品信息成功");
    }

//    /**
//     * 根据条件查询对应的菜品数据
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){//这里参数为什么不是 Long categoryId，是为了让此方法的通用性更强，传过来其它字段也可以复用
//        //构造查询条件
//        LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);//添加条件，查询状态为1(起售)
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    /**
     * 根据条件查询对应的菜品数据
     * 这里集合内的泛型数据改为DishDto，以便页面能够既展示菜品的基本信息，又展示菜品的口味信息
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){//这里参数为什么不是 Long categoryId，是为了让此方法的通用性更强，传过来其它字段也可以复用
        List<DishDto> dishDtoList=null;

        //动态构造key
        String key ="dish_"+dish.getCategoryId()+"_"+dish.getStatus();//dish_id_1

        //先从redis中获取缓存数据
       dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果存在，直接返回，无需查询数据库
        if (dishDtoList!=null){
            return R.success(dishDtoList);
        }


        //构造查询条件
        LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);//添加条件，查询状态为1(起售)
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);


        List<Dish> list = dishService.list(queryWrapper);


        dishDtoList = list.stream().map((item)->{
            DishDto dishDto =new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> wrapper =new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

}









