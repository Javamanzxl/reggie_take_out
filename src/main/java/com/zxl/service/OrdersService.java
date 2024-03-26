package com.zxl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.common.R;
import com.zxl.pojo.OrderDetail;
import com.zxl.pojo.Orders;

import javax.servlet.http.HttpSession;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/11 14:09
 */
public interface OrdersService extends IService<Orders> {
    R<String> submit(HttpSession session, Orders orders);

    R<Page<Orders>> userPage(HttpSession session,int page, int pageSize);
}
