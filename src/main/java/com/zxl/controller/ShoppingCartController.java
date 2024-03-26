package com.zxl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zxl.common.R;
import com.zxl.pojo.ShoppingCart;
import com.zxl.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/11 10:47
 */
@RestController
@RequestMapping("shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 购物车新建
     * @param shoppingCart
     * @return
     */
    @PostMapping("add")
    public R<ShoppingCart> add(HttpSession session, @RequestBody ShoppingCart shoppingCart){
        R<ShoppingCart> result = shoppingCartService.add(session,shoppingCart);
        return result;
    }

    /**
     * 查询购物车
     * @param session
     * @return
     */
    @GetMapping("list")
    public R<List<ShoppingCart>> list(HttpSession session){
        Long user = (Long)session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,user);
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @param session
     * @return
     */
    @DeleteMapping("clean")
    public R<String> clean(HttpSession session){
        R result = shoppingCartService.clean(session);
        return result;
    }

    /**
     * 购物车内商品数量减少
     * @param session
     * @param shoppingCart
     * @return
     */
    @PostMapping("sub")
    public R<ShoppingCart> sub(HttpSession session,@RequestBody ShoppingCart shoppingCart){
        R result = shoppingCartService.sub(session,shoppingCart);
        return  result;
    }
}
