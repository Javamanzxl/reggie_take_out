package com.zxl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.common.R;
import com.zxl.pojo.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     * @return
     */
    R<String> removeCheckById(Long id);

    R<Page> queryCategoryByPage(int page, int pageSize);
}
