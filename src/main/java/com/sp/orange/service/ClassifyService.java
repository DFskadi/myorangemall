package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.model.Classify;

import java.util.List;

public interface ClassifyService extends IService<Classify> {
    Page page(int page, int pageSize);

    void removeByDS(Long id);

    List<Classify> queryDishByID(Classify classify);

    void categorySave(Classify classify);

    String update(Classify classify);
}
