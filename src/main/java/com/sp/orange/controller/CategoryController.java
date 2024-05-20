package com.sp.orange.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.R;
import com.sp.orange.entity.Category;
import com.sp.orange.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
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
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.categorySave(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页构造
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {//这里的page，pageSize,为分页请求携带的参数
        Page pageInfo = categoryService.page(page, pageSize);
        return R.success(pageInfo);
    }

    //删除分类业务
    @DeleteMapping()
    public R<String> delete(Long id) {
        categoryService.removeByDS(id);
        return R.success("删除分类成功");
    }

    //修改分类业务
    @PutMapping()
    public R<String> update(@RequestBody Category category) {//前端传入的数据
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    //查询菜品分类信息
    @GetMapping("/list")
    public R<List<Category>> queryDishByID(Category category) {
        List<Category> dishList = categoryService.queryDishByID(category);
        return R.success(dishList);
    }


}

