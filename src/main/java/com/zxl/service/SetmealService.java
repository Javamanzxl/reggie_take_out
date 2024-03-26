package com.zxl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.common.R;
import com.zxl.dto.SetmealDto;
import com.zxl.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    R<String> saveByDto(SetmealDto setmealDto);

    R<Page> pageBySetmeal(int page, int pageSize, String name);

    R deleteByStatus(String ids);

    R updateByStatus(int st, String ids);

    R getDate(Long id);

    R updaetSetmeal(SetmealDto setmealDto);

    R<List<SetmealDto>> listByIdAndStatus(Setmeal setmeal);
}
