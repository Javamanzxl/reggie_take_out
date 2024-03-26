package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.CustomException;
import com.zxl.common.R;
import com.zxl.mapper.CategoryMapper;
import com.zxl.pojo.Category;
import com.zxl.pojo.Dish;
import com.zxl.pojo.Setmeal;
import com.zxl.service.CategoryService;
import com.zxl.service.DishService;
import com.zxl.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/03 11:27
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public R<String> removeCheckById(Long id) {
        //查询当前分类是否关联了菜品，如果已关链，抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count > 0){
            //如果已关链，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已关联，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1 > 0){
            //如果已关链，抛出异常
            throw new CustomException("当前套餐下关联了菜品，不能删除");
        }
        categoryMapper.deleteById(id);
        return R.success("删除成功");
    }

    @Override
    public R<Page> queryCategoryByPage(int page, int pageSize) {
        Page<Category> categoryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryMapper.selectPage(categoryPage,categoryLambdaQueryWrapper);
        return R.success(categoryPage);
    }
}
