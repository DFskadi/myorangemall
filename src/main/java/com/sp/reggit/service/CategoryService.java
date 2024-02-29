package com.sp.reggit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.reggit.entity.Category;

public interface CategoryService extends IService<Category> {
    public void removeByDS(Long id);
}
