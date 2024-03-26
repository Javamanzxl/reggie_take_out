package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.BaseContext;
import com.zxl.common.R;
import com.zxl.mapper.ShoppingCartMapper;
import com.zxl.pojo.ShoppingCart;
import com.zxl.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/11 10:49
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public R<ShoppingCart> add(HttpSession session,ShoppingCart shoppingCart) {
        //设置用户id
        Long userId = (Long)session.getAttribute("user");
        //Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        if (dishId != null) {
            //添加到购物车的是菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else{
            //添加到购物车的是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询当前菜品或套餐是否在购物车中
        ShoppingCart shoppingCart1 = shoppingCartMapper.selectOne(shoppingCartLambdaQueryWrapper);
        //如果已经存在，就在原来数量基础上+1
        //不存在添加到购物车，数量默认是1
        if(shoppingCart1 != null){
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.updateById(shoppingCart1);
        }else{
            shoppingCart.setNumber(1);
            shoppingCartMapper.insert(shoppingCart);
            shoppingCart1 = shoppingCart;
        }
        return R.success(shoppingCart1);
    }

    @Override
    public R clean(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartMapper.delete(shoppingCartLambdaQueryWrapper);
        return R.success("清空成功");
    }

    @Override
    public R sub(HttpSession session, ShoppingCart shoppingCart) {
        Long userId = (Long)session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        ShoppingCart shoppingCart1 =    null;
        if(shoppingCart.getDishId()!=null){
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            shoppingCart1 = shoppingCartMapper.selectOne(shoppingCartLambdaQueryWrapper);
            if(shoppingCart1.getNumber() ==1){
                shoppingCartMapper.deleteById(shoppingCart1);
            }else{
                shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
            }
        }else{
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            shoppingCart1 = shoppingCartMapper.selectOne(shoppingCartLambdaQueryWrapper);
            if(shoppingCart1.getNumber() == 1){
                shoppingCartMapper.deleteById(shoppingCart1);
            }else{
                shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
            }
        }

        return R.success(shoppingCart1);
    }
}
