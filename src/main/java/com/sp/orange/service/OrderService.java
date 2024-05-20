package com.sp.orange.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sp.orange.entity.Orders;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService extends IService<Orders> {
    @Transactional
    public void submit(Orders orders,Long userId);

    Page userPage(int page, int pageSize,Long userId);

    Page page(int page, int pageSize, String number, String beginTime, String endTime);

    String status(Long id);

    String againSubmit(Long orderId,Long userId);
}
