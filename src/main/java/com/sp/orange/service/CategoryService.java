package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.entity.Category;

public interface CategoryService extends IService<Category> {
    public void removeByDS(Long id);
}
