package com.sp.orange.dto;

import com.sp.orange.entity.OrderDetail;
import com.sp.orange.entity.Orders;
import lombok.Data;

import java.util.List;
@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
