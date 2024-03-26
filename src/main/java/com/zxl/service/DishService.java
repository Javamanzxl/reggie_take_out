package com.zxl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.common.R;
import com.zxl.dto.DishDto;
import com.zxl.pojo.Dish;

import java.util.List;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/03 14:46
 */
public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时插入菜品对应的口味数据，操作两张表:dish、dish_flavor
     * @param dishDto
     * @return
     */
    R<String> saveWithFlavor(DishDto dishDto);

    R<Page> queryDishByPage(int size, int pageSize, String name);

    R<DishDto> queryById(Long id);

    R<String> updateWithFlavor(DishDto dishDto);

    R delete(String ids);

    R<String> updateStatus(int st, String ids);

    R<List<DishDto>> listByIdOrName(Dish dish);
}
