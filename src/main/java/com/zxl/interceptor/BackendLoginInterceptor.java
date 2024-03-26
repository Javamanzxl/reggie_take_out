package com.zxl.interceptor;

import com.alibaba.fastjson.JSON;
import com.zxl.common.BaseContext;
import com.zxl.common.R;
import com.zxl.mapper.EmployeeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ：xxx
 * @description：TODO 后端登录保护的拦截器
 * @date ：2024/02/01 14:46
 */
@Slf4j
@Component
public class BackendLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute("employee") == null) {
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            response.sendRedirect("/backend/page/login/login.html");
            return false;
        }

        Long empId = (Long) request.getSession().getAttribute("employee");
        //在线程中存放id
        BaseContext.setCurrentId(empId);
        return true;

    }
}
