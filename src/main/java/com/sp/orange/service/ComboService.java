package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.dto.CuisineDto;
import com.sp.orange.dto.ComboDto;
import com.sp.orange.model.Combo;

import java.util.List;


public interface ComboService extends IService<Combo> {
    List<Combo> list(Combo combo);

    void saveWithCuisine(ComboDto setmealDto);

    void removeWithDish(List<Long> ids);

    ComboDto getByIdWithDish(Long id);

    Page page(String name, int page, int pageSize);

    void updateWithDish(ComboDto setmealDto);

    String status(Integer status,List<Long> ids);

    List<CuisineDto> dish(Long setmealId);
}
