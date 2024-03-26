package com.zxl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.common.R;
import com.zxl.pojo.ShoppingCart;

import javax.servlet.http.HttpSession;

public interface ShoppingCartService extends IService<ShoppingCart> {
    R sub(HttpSession session, ShoppingCart shoppingCart);
    R<ShoppingCart> add(HttpSession session, ShoppingCart shoppingCart);

    R clean(HttpSession session);
}
