package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.R;
import com.zxl.mapper.EmployeeMapper;
import com.zxl.service.EmployeeService;
import com.zxl.pojo.Employee;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/01/31 18:05
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public R login(HttpServletRequest request, Employee employee) {
        //密码md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据用户名查询
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeMapper.selectOne(employeeLambdaQueryWrapper);
        if (emp == null) {
            return R.error("登陆失败");
        }
        //密码比对
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }
        //检查员工状态
        if (emp.getStatus() == 0) {
            return R.error("登陆失败");
        }
        //讲员工id放入session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * @param request
     * @param employee
     * @return
     */
    @Override
    public R addNewEmployee(HttpServletRequest request, Employee employee) {
        //获取创建者id，并设置创建者
        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
        //设置初始密码 md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeMapper.insert(employee);


        return R.success("新增员工成功");
    }

    @Override
    public R<Page> queryEmployeeByPage(int page, int pageSize, String name) {
        Page<Employee> employeePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        employeeLambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeMapper.selectPage(employeePage, employeeLambdaQueryWrapper);
        return R.success(employeePage);
    }

    @Override
    public R<String> updateStatusById(HttpServletRequest request, Employee employee) {

        Long employeeId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(employeeId);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeMapper.updateById(employee);
        if (employee.getStatus() == 1) {
            return R.success("启用成功");
        }
        return R.success("禁用成功");
    }
}
