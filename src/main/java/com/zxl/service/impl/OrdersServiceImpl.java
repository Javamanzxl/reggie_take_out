package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.CustomException;
import com.zxl.common.R;
import com.zxl.mapper.*;
import com.zxl.pojo.*;
import com.zxl.service.OrderDetailService;
import com.zxl.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/11 14:10
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;

    @Transactional
    @Override
    public R<String> submit(HttpSession session, Orders orders) {
        //获取用户id
        Long userId =(Long)session.getAttribute("user");
        //查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(shoppingCartLambdaQueryWrapper);
        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new CustomException("购物车为空,不能下单");
        }
        //向订单表插入数据，1条数据
            //查询用户姓名 -> user表
        User user = userMapper.selectById(userId);
            //查地址信息 -> address表
        AddressBook addressBook = addressBookMapper.selectById(orders.getAddressBookId());
        String detail = addressBook.getDetail();
        if(detail == null){
            throw new CustomException("地址信息有误，请重新填写");
        }
        //设置订单号等其他信息


        long orderId = IdWorker.getId();
        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);

        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCartList.stream().map(item->{
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
            item.getNumber();
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//计算购物车总金额
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(detail);
        ordersMapper.insert(orders);
        //向订单明细表插入数据，多条数据

        orderDetailService.saveBatch(orderDetails);
        //清除购物车数据
        shoppingCartMapper.delete(shoppingCartLambdaQueryWrapper);
        return R.success("下单成功");
    }

    @Override
    public R<Page<Orders>> userPage(HttpSession session,int page, int pageSize) {
        Long userId = (Long)session.getAttribute("user");
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId,userId);
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        ordersMapper.selectPage(ordersPage,ordersLambdaQueryWrapper);
        return R.success(ordersPage);
    }
}
