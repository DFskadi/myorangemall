package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.common.BaseContext;
import com.sp.orange.common.CustomException;
import com.sp.orange.common.R;
import com.sp.orange.dto.OrderDto;
import com.sp.orange.entity.*;
import com.sp.orange.mapper.OrderMapper;
import com.sp.orange.service.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShopingCartService shopingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    public Page page(int page, int pageSize, String number, String beginTime, String endTime) {
        //创建分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.like(number != null, Orders::getNumber, number)
                .gt(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime)
                .lt(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);
        //执行查询业务
        this.page(pageInfo, lambdaQueryWrapper);
        return pageInfo;
    }

    /**
     * 从OrderDto分页查询抽离的一个方法，通过订单id查询订单明细，得到一个订单明细的集合
     *这里抽离出来是为了避免在stream中遍历的时候直接使用构造条件来查询导致eq叠加，从而导致后面查询的数据都是nul
     * @param orderId
     * @return
     */
    public List<OrderDetail> getListByOrderId(Long orderId){
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> list = orderDetailService.list(queryWrapper);
        return list;
    }


    /**
     * 前端用户浏览订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page userPage(int page, int pageSize,Long userId) {

//        Long userId = BaseContext.getetCurrentId();
//        Page pageInfo = new Page(page, pageSize);
//        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Orders::getUserId, userId);
//        wrapper.orderByDesc(Orders::getOrderTime);
//
//        this.page(pageInfo, wrapper);
//        return pageInfo;
        //分页构造器对象
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Page<OrderDto> pageDto=new Page<>(page,page);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        //这里是直接把当前用户分页的全部结果查询出来，要添加用户id，否则出现该用户查询到其它用户订单数据
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        this.page(pageInfo,queryWrapper);

        //通过OrderId查询对应的OrderDetail
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //对OrderDto进行需要的属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList=records.stream().map((item)->{
            //new出来的orderDto，属性值都是空的
            OrderDto orderDto=new OrderDto();
            //获取订单id
            Long orderId = item.getId();
            List<OrderDetail> OrderDetailList = this.getListByOrderId(orderId);
            //对orderDto进行orderDetails属性的赋值
            BeanUtils.copyProperties(item,orderDto);
            orderDto.setOrderDetails(OrderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(pageInfo,pageDto,"records");//第三个参数是忽略的属性
        pageDto.setRecords(orderDtoList);
        return pageDto;
    }

    /**
     * 用户下单
     *
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders,Long userId) {
        //获取当前用户id
        //查询当前用户的购物车信息
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);

        List<ShoppingCart> cartList = shopingCartService.list(wrapper);
        System.out.println(cartList);

        if (cartList == null || cartList.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(userId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，不能下单");
        }


        //向订单表插入数据,一条数据
        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);//原子变量，即使在多线程的条件下也不会计算出错,直接Int、double在多线程环境下可能计算出错
        List<OrderDetail> orderDetails = cartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "1" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "1" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "1" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "1" : addressBook.getDetail()));


        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        //清空购物车数据
        shopingCartService.remove(wrapper);
    }

    /**
     * 订单状态修改
     * @return
     */
    @Override
    public String status(Long id){
//        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Orders::getId,id);
//        Orders orderone = this.getOne(lambdaQueryWrapper);

        LambdaUpdateWrapper<Orders> queryWrapper =new LambdaUpdateWrapper<>();

        queryWrapper.eq(id !=null,Orders::getId,id).set(Orders::getStatus,4);
        this.update(queryWrapper);
        return "派送成功";
    }

    /**
     * 前端点击再来一单是直接跳转到购物车的，所以为了避免数据有问题，再跳转之前我们需要把购物车的数据给清除
     * ①通过orderId获取订单明细
     * ②把订单明细的数据的数据塞到购物车表中，不过在此之前要先把购物车表中的数据给清除(清除的是当前登录用户的购物车表中的数据)，
     * 不然就会导致再来一单的数据有问题；
     * (这样可能会影响用户体验，但是对于外卖来说，用户体验的影响不是很大，电商项目就不能这么干了)
     */
    @Override
    public String againSubmit(Long orderId,Long userId){
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orderId);
        //获取订单对应的所有订单明细
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        //通过用户id把原来的购物车给清空
        shopingCartService.clean(userId);
        //把从order表中和order_details表中获取的数据赋值给这个购物车对象
           List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item)->{
            ShoppingCart shoppingCart=new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if(dishId!=null){
                shoppingCart.setDishId(dishId);
            }else {
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

           //把携带数据的购物车批量插入购物车表
        shopingCartService.saveBatch(shoppingCartList);
        return "操作成功";

    }


}
