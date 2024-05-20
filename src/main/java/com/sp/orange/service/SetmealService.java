package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.dto.DishDto;
import com.sp.orange.dto.SetmealDto;
import com.sp.orange.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    List<Setmeal> list(Setmeal setmeal);

    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    SetmealDto getByIdWithDish(Long id);

    Page page(String name, int page, int pageSize);

    void updateWithDish(SetmealDto setmealDto);

    String status(Integer status,List<Long> ids);

    List<DishDto> dish(Long setmealId);
}
