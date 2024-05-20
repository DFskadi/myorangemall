package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    Page page(int page, int pageSize);

    void removeByDS(Long id);

    List<Category> queryDishByID(Category category);

    void categorySave(Category category);
}
