package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.CustomException;
import com.zxl.common.R;
import com.zxl.dto.DishDto;
import com.zxl.mapper.CategoryMapper;
import com.zxl.mapper.DishMapper;
import com.zxl.pojo.Category;
import com.zxl.pojo.Dish;
import com.zxl.pojo.DishFlavor;
import com.zxl.service.DishFlavorService;
import com.zxl.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/03 14:47
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional //操作两张表时，最好加上事务处理
    public R<String> saveWithFlavor(DishDto dishDto) {
        //保存基本信息
        this.save(dishDto);
        Long dishId = dishDto.getId();//菜品id
        //保存菜品口味数据到菜品口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((flavor) -> {
            flavor.setDishId(dishId);
            return flavor;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        //清除所有菜品的缓存数据
        redisTemplate.delete(redisTemplate.keys("dish_*"));
        return R.success("保存成功");
    }

    @Override
    public R<Page> queryDishByPage(int page, int pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishMapper.selectPage(dishPage, dishLambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        //获取分页查询的包装的数据
        List<Dish> dishRecords = dishPage.getRecords();
        //用DishDto工具实体写一个集合装载Records
        List<DishDto> dishDtoRecords = null;
        //把dishRecords中的数据加上categoryName装到dishDtoRecords中
        dishDtoRecords = dishRecords.stream().map((dishRecord) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dishRecord, dishDto);
            Long categoryId = dishRecord.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        //给dishDtoPage装载dishDtoRecords
        dishDtoPage.setRecords(dishDtoRecords);
        return R.success(dishDtoPage);

    }

    @Override
    public R<DishDto> queryById(Long id) {
        //查询菜品基本信息
        Dish dish = dishMapper.selectById(id);
        //对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(flavors);
        return R.success(dishDto);
    }

    @Transactional
    @Override
    public R<String> updateWithFlavor(DishDto dishDto) {
        //对象拷贝
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish, "flavors");
        //更新菜品基本信息 dish表
        dishMapper.updateById(dish);
        //更新菜品口味信息
        //清除当前菜品对应口味
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        //对象拷贝,然后再添加新的口味
        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        dishFlavors = dishFlavors.stream().map((flavor) -> {
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavors);

        //清理某个分类下的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId()+"_*";
        redisTemplate.delete(key);

        return R.success("保存成功");
    }

    @Override
    public R delete(String ids) {
        String[] splitIds = ids.split(",");
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId, splitIds);
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(dishLambdaQueryWrapper);
        if (count > 0) {
            //如果有商品在售，抛出异常
            throw new CustomException("当前商品正在售卖，不能删除");
        }
        //删除dish表中的数据
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper1.in(Dish::getId, splitIds);
        this.remove(dishLambdaQueryWrapper1);
        //删除dishFlavor表中的数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, splitIds);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        //清除所有菜品的缓存数据
        redisTemplate.delete(redisTemplate.keys("dish_*"));
        return R.success("删除成功");
    }

    @Override
    public R<String> updateStatus(int st, String ids) {
        String[] splitIds = ids.split(",");
        for (String splitId : splitIds) {
            Dish dish = dishMapper.selectById(splitId);
            dish.setStatus(st);
            dishMapper.updateById(dish);
        }
        //清除所有菜品的缓存数据
        redisTemplate.delete(redisTemplate.keys("dish_*"));
        return R.success("修改成功");
    }

    @Override
    public R<List<DishDto>> listByIdOrName(Dish dish) {
        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_123_1
        //先从Redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果存在直接返回
        if(dishDtoList != null){
            return R.success(dishDtoList);
        }

        //如果不存在，需要查询数据库，将查询到的菜品缓存到Redis
        //根据菜品分类id查菜品
        //查询菜品基本信息
        if (dish.getCategoryId() != null) {
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(Dish::getCategoryId, dish.getCategoryId())
                    .eq(Dish::getStatus, 1)
                    .orderByDesc(Dish::getUpdateTime);
            List<Dish> dishList = dishMapper.selectList(dishLambdaQueryWrapper);
            dishDtoList = dishList.stream().map(item -> {
                //拷贝dish的基本信息到dishDto
                DishDto dishDto = new DishDto();
                BeanUtils.copyProperties(item, dishDto);
                //设置DishDto菜品分类名称(categoryName)
                Category category = categoryMapper.selectById(item.getCategoryId());
                if (category != null) {
                    dishDto.setCategoryName(category.getName());
                }
                //设置dishDto的flavors
                LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
                List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
                dishDto.setFlavors(dishFlavorList);
                return dishDto;
            }).collect(Collectors.toList());
            redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
            return R.success(dishDtoList);
        }
        //根据菜品分类name查菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getName, dish.getName())
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishMapper.selectList(dishLambdaQueryWrapper);
        dishDtoList = dishList.stream().map(item -> {
            //拷贝dish的基本信息到dishDto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //设置DishDto菜品分类名称(categoryName)
            Category category = categoryMapper.selectById(item.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            //设置dishDto的flavors
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);


    }
}
