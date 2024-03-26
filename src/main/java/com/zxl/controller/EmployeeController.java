package com.zxl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxl.common.R;
import com.zxl.pojo.Employee;
import com.zxl.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/01/31 18:04
 */
@RestController
@Slf4j
@RequestMapping("employee")
@CrossOrigin
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        R result = employeeService.login(request,employee);
        return result;
    }

    /**
     * 退出功能
     * @param request
     * @return
     */
    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request){
        //清除Session中保存的当前登陆员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增用户
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> addNewEmployee(HttpServletRequest request, @RequestBody Employee employee){
        R result = employeeService.addNewEmployee(request,employee);
        return result;

    }

    @GetMapping("page")
    public R<Page> queryEmployeeByPage(int page,int pageSize,String name){
        R<Page> result = employeeService.queryEmployeeByPage(page,pageSize,name);
        return result;
    }

    /**
     * 禁用状态启用状态转转
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updateStatusById(HttpServletRequest request,@RequestBody Employee employee){
        R<String> result = employeeService.updateStatusById(request,employee);
        return result;
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return  R.error("无该用户信息");

    }

}
