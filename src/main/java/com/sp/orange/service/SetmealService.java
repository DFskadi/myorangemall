package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.dto.SetmealDto;
import com.sp.orange.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);
}
