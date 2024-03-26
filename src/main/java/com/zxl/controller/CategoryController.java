package com.zxl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.common.R;
import com.zxl.pojo.Category;
import com.zxl.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/03 11:26
 */
@RestController
@Slf4j
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("保存成功");
    }
    @GetMapping("page")
    public R<Page> queryCategoryByPage(int page,int pageSize){
        R<Page> result = categoryService.queryCategoryByPage(page,pageSize);
        return result;
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        R<String> result = categoryService.removeCheckById(id);
        //categoryService.removeById(id);
        return result;
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }


    @GetMapping("list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        categoryLambdaQueryWrapper.orderByAsc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(categoryLambdaQueryWrapper);
        return R.success(categoryList);
    }
}
