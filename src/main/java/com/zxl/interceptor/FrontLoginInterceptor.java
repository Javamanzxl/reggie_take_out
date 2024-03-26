package com.zxl.interceptor;

import com.alibaba.fastjson.JSON;
import com.zxl.common.BaseContext;
import com.zxl.common.R;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ：xxx
 * @description：TODO 用户端登录保护拦截器
 * @date ：2024/02/05 11:53
 */
@Component
public class FrontLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute("user") == null) {
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            response.sendRedirect("/front/page/login.html");
            return false;
        }
        Long userId = (Long) request.getSession().getAttribute("user");
        //在线程中存放id
        BaseContext.setCurrentId(userId);
        return true;

    }
}
