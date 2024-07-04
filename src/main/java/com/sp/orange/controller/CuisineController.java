package com.sp.orange.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.R;
import com.sp.orange.dto.CuisineDto;
import com.sp.orange.model.Cuisine;
import com.sp.orange.service.ClassifyService;
import com.sp.orange.service.DishFlavorService;
import com.sp.orange.service.CuisineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/cuisine")
@Slf4j
public class CuisineController {
    @Autowired
    private CuisineService cuisineService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private ClassifyService classifyService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping
    public R<String> save(@RequestBody CuisineDto cuisineDto) {//前端通过json传递给服务端数据
        cuisineService.saveWithFlavor(cuisineDto);
        //清理所有菜品的缓存
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
        //精确清理
        String key = "cuisine_" + cuisineDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }


    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<CuisineDto> cuisineDtoPage = cuisineService.page(page, pageSize, name);
        return R.success(cuisineDtoPage);
    }


    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<CuisineDto> get(@PathVariable Long id) {//这里使用DishDto作为泛型的原因是由于，回显的数据除了dish中的字段，同样包含口味等dish中不存在的字段
        CuisineDto cuisineDto = cuisineService.getByIdWithFlavor(id);
        return R.success(cuisineDto);
    }


    @PutMapping
    public R<String> update(@RequestBody CuisineDto cuisineDto){
        cuisineService.updateWithFlavor(cuisineDto);
        String key="cuisine_"+cuisineDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改菜品信息成功");
    }

    /**
     * 删除菜品(逻辑删除)
     * @param ids
     * @return
     */
    @DeleteMapping
    R<String> delete(@RequestParam List<Long> ids) {
        cuisineService.deleteWithFlavor(ids);
        return R.success("批量删除成功");
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
     * @param cuisine
     * @return
     */
    @GetMapping("/list")
    public R<List<CuisineDto>> list(Cuisine cuisine) {//这里参数为什么不是 Long categoryId，是为了让此方法的通用性更强，传过来其它字段也可以复用
        List<CuisineDto> cuisineDtoList = null;
        //动态构造key
        String key = "cuisine_" + cuisine.getCategoryId() + "_1";//dish_id_1
        //先从redis中获取缓存数据
        cuisineDtoList = (List<CuisineDto>) redisTemplate.opsForValue().get(key);
        //如果存在，直接返回，无需查询数据库
        if (cuisineDtoList != null) {
            return R.success(cuisineDtoList);
        }

        cuisineDtoList = cuisineService.list(cuisine);
        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set(key,cuisineDtoList,60, TimeUnit.MINUTES);
        return R.success(cuisineDtoList);
    }

    /**
     * 修改菜品的状态，当菜品在套餐中，不能进行停售操作
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        List<Cuisine> cuisineList = cuisineService.status(status, ids);
        for (Cuisine cuisine : cuisineList) {
            String key = "cuisine_" + cuisine.getCategoryId() + "_1";
            redisTemplate.delete(key);
        }

        return R.success("批量操作成功");
    }

}









