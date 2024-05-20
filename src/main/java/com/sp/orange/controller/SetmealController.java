package com.sp.orange.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.CustomException;
import com.sp.orange.common.R;
import com.sp.orange.dto.DishDto;
import com.sp.orange.dto.SetmealDto;
import com.sp.orange.entity.Category;
import com.sp.orange.entity.Dish;
import com.sp.orange.entity.Setmeal;
import com.sp.orange.service.CategoryService;
import com.sp.orange.service.DishService;
import com.sp.orange.service.SetmealDishService;
import com.sp.orange.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping()
    @ApiOperation("新增套餐接口")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "name", value = "套餐名称", required = false)
    })
    public R<Page> page(String name, int page, int pageSize) {
        Page dtoPage = setmealService.page(name, page, pageSize);
        return R.success(dtoPage);
    }


    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)//allEntries = true ，清理setmealCache分类下的所有缓存数据
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info(String.valueOf(ids.size()));
        if (ids.size()==0){
           // throw new CustomException("批量操作，请先勾选操作套餐！");
            return R.error("批量操作，请先勾选操作套餐！");
        }
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        List<Setmeal> list = setmealService.list(setmeal);
        return R.success(list);
    }


    /**
     * 套餐数据回显
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealdto = setmealService.getByIdWithDish(id);
        return R.success(setmealdto);
    }


    /**
     * 修改套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        if (setmealDto == null) {
            return R.error("请求异常");
        }
        if (setmealDto.getSetmealDishes() == null) {
            return R.error("套餐没有菜品,请添加套餐");
        }
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐信息成功");
    }

    /**
     * 更新套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("更新套餐状态接口")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        String str = setmealService.status(status, ids);
        return R.success(str);
    }


    /**
     * 点击套餐图片展示详情
     * @param setmealId
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable("id") Long setmealId){
        List<DishDto> dishDtoList = setmealService.dish(setmealId);
        return R.success(dishDtoList);
    }




}









