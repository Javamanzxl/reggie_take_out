package com.zxl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.common.R;
import com.zxl.pojo.Employee;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/01/31 18:05
 */

public interface EmployeeService extends IService<Employee> {
    R login(HttpServletRequest request,Employee employee);

    R addNewEmployee(HttpServletRequest request, Employee employee);

    R<Page> queryEmployeeByPage(int page, int pageSize, String name);

    R<String> updateStatusById(HttpServletRequest request,Employee employee);
}
