package com.sp.reggit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.reggit.common.R;
import com.sp.reggit.entity.Category;
import com.sp.reggit.entity.Dish;
import com.sp.reggit.service.CategoryService;
import com.sp.reggit.service.DishService;
import com.sp.reggit.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    //   /category/page
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){//这里的page，pageSize,为分页请求携带的参数
//        log.info("page ={},pageSize={}", page,pageSize);
        //创建分页构造器
        Page pageInfo= new Page(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper =new LambdaQueryWrapper<>();
        //以sort属性为排序条件
        queryWrapper.orderByDesc(Category::getSort);
        //执行查询业务
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    //删除分类业务
    @DeleteMapping()
    public R<String> delete(Long id){
       categoryService.removeByDS(id);
        return R.success("删除分类成功");
    }

    //修改分类业务
    @PutMapping()
    public R<String> update(@RequestBody Category category){//前端传入的数据

        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    //查询菜品分类信息
    @GetMapping("/list")
    public R<List<Category>> queryDishByID(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper();
        //添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> dishList = categoryService.list(lambdaQueryWrapper);

        return R.success(dishList);
    }



}

