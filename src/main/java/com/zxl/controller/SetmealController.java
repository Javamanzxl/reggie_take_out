package com.zxl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.common.R;
import com.zxl.dto.SetmealDto;
import com.zxl.pojo.Setmeal;
import com.zxl.service.SetmealDishService;
import com.zxl.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：xxx
 * @description：TODO 套餐功能
 * @date ：2024/02/04 17:19
 */
@RestController
@RequestMapping("setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 套餐新增保存
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        R<String> result = setmealService.saveByDto(setmealDto);
        return result;
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public R<Page> page(int page,int pageSize,String name){
        R<Page> result = setmealService.pageBySetmeal(page,pageSize,name);
        return result;
    }

    /**
     * 批量或单个删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids){
        R result = setmealService.deleteByStatus(ids);
        return result;
    }

    @PostMapping("status/{st}")
    public R<String> updateByStatus(@PathVariable int st , String ids){
        R result = setmealService.updateByStatus(st,ids);
        return result;
    }

    /**
     * 修改页面回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getDate(@PathVariable Long id){
        R result = setmealService.getDate(id);
        return result;
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        R result = setmealService.updaetSetmeal(setmealDto);
        return result;
    }

    @GetMapping("list")
    public R<List<SetmealDto>> list(Setmeal setmeal){
        R<List<SetmealDto>> result = setmealService.listByIdAndStatus(setmeal);
        return result;
    }
}
