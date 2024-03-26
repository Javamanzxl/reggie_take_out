package com.zxl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.zxl.common.R;
import com.zxl.dto.DishDto;
import com.zxl.pojo.Category;
import com.zxl.pojo.Dish;
import com.zxl.pojo.DishFlavor;
import com.zxl.service.CategoryService;
import com.zxl.service.DishFlavorService;
import com.zxl.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/03 16:20
 */
@RestController
@RequestMapping("dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        R<String> result = dishService.saveWithFlavor(dishDto);
        return result;
    }

    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public R<Page> queryDishByPage(int page, int pageSize, String name) {
        R<Page> result = dishService.queryDishByPage(page, pageSize, name);
        return result;
    }

    /**
     * 根据id查询菜品信息和口味
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> queryById(@PathVariable Long id) {
        R<DishDto> result = dishService.queryById(id);
        return result;
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        R<String> result = dishService.updateWithFlavor(dishDto);
        return result;
    }

    /**
     * 删除和批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        R result = dishService.delete(ids);
        return result;
    }

    /**
     * 批量或者单个修改状态
     *
     * @param st
     * @param ids
     * @return
     */
    @PostMapping("/status/{st}")
    public R<String> updateStatus(@PathVariable int st, String ids) {
        R<String> result = dishService.updateStatus(st, ids);
        return result;
    }

    /**
     * 根据菜品分类id查询菜品数据
     */

 /*   @GetMapping("list")
    public R<List<Dish>> list(Dish dish){
        if(dish.getCategoryId() != null) {
            //根据CategoryId查询
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
            dishLambdaQueryWrapper.eq(Dish::getStatus,1);
            dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
            List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
            return R.success(dishList);
        }*/
    @GetMapping("list")
    public R<List<DishDto>> list(Dish dish) {
        R<List<DishDto>> result = dishService.listByIdOrName(dish);
        return result;
    }


}
