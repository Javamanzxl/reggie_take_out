package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.CustomException;
import com.zxl.common.R;
import com.zxl.dto.SetmealDto;
import com.zxl.mapper.CategoryMapper;
import com.zxl.mapper.SetmealMapper;
import com.zxl.pojo.Category;
import com.zxl.pojo.Setmeal;
import com.zxl.pojo.SetmealDish;
import com.zxl.service.SetmealDishService;
import com.zxl.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/03 14:48
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional
    @Override
    @CacheEvict(value = "setmealDtoCache",allEntries = true) //删除该缓存中的所有数据
    public R<String> saveByDto(SetmealDto setmealDto) {
        //对象拷贝,保存套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal, "setmealDishes");
        this.save(setmeal);
        //保存套餐关联菜品信息 dishMealDish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
        return R.success("保存成功");
    }

    @Override
    public R<Page> pageBySetmeal(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //不包含套餐分类名称的查询结果
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealMapper.selectPage(setmealPage, setmealLambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        //
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtoRecords = records.stream().map(record -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);
            //查询套餐分类名称 ->category表
            Category category = categoryMapper.selectById(record.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoRecords);
        return R.success(setmealDtoPage);
    }

    @Override
    @CacheEvict(value = "setmealDtoCache",allEntries = true) //删除该缓存中的所有数据
    public R deleteByStatus(String ids) {
        String[] splitIds = ids.split(",");
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, splitIds);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLambdaQueryWrapper);
        if (count > 0) {
            throw new CustomException("该套餐未停售，无法删除");
        }
        //删除setmeal表中数据
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper1.in(Setmeal::getId, splitIds);
        this.remove(setmealLambdaQueryWrapper1);
        //删除setmealDish中的数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, splitIds);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        return R.success("删除成功");
    }

    @Override
    @CacheEvict(value = "setmealDtoCache",allEntries = true) //删除该缓存中的所有数据
    public R updateByStatus(int st, String ids) {
        String[] splitIds = ids.split(",");
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, splitIds);
        for (String splitId : splitIds) {
            Setmeal setmeal = this.getById(splitId);
            setmeal.setStatus(st);
            this.updateById(setmeal);
        }

        return R.success("修改成功");
    }

    @Override
    public R getDate(Long id) {
        Setmeal setmeal = this.getById(id);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(setmealDishLambdaQueryWrapper);
        SetmealDto setmealDto = new SetmealDto();
        setmealDto.setSetmealDishes(setmealDishList);
        BeanUtils.copyProperties(setmeal, setmealDto);
        return R.success(setmealDto);
    }

    @Override
    public R updaetSetmeal(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        //更新套餐基本信息
        this.updateById(setmeal);
        //更新套餐关联菜品信息
        //删除关联菜品信息 -> setmealDish
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //添加关联菜品信息 -> setmealDish
        setmealDishService.saveBatch(setmealDishes);

        return R.success("修改成功");
    }

    @Override
    @Cacheable(value = "setmealDtoCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<SetmealDto>> listByIdAndStatus(Setmeal setmeal) {
        //查询套餐的基本信息
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealMapper.selectList(setmealLambdaQueryWrapper);
        List<SetmealDto> setmealDtoList = setmealList.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, item.getId());
            List<SetmealDish> setmealDishList = setmealDishService.list(setmealDishLambdaQueryWrapper);
            setmealDto.setSetmealDishes(setmealDishList);
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(setmealDtoList);

    }
}
