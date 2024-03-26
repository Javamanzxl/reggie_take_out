package com.zxl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.common.R;
import com.zxl.pojo.OrderDetail;
import com.zxl.pojo.Orders;
import com.zxl.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/11 14:05
 */
@RestController
@RequestMapping("order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 订单提交
     * @param session
     * @param orders
     * @return
     */
    @PostMapping("submit")
    public R<String> submit(HttpSession session, @RequestBody Orders orders){
        R<String> result = ordersService.submit(session,orders);
        return result;
    }

    @GetMapping("userPage")
    public R<Page<Orders>> userPage(HttpSession session ,int page,int pageSize){
        R<Page<Orders>> result = ordersService.userPage(session,page,pageSize);
        return result;
    }
}
