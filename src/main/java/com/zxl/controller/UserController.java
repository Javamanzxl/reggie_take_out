package com.zxl.controller;

import com.zxl.common.R;
import com.zxl.pojo.User;
import com.zxl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/05 9:59
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码
     * @return
     */
    @PostMapping("sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        R<String> result = userService.sendMsg(user,session);
        return result;

    }

    /**
     * 移动端用户登录
     * @param userMap
     * @param session
     * @return
     */
    @PostMapping("login")
    public R<String> login(@RequestBody Map userMap, HttpSession session){
        R<String> result = userService.login(userMap,session);
        return result;
    }

    /**
     * 退出登录
     * @param httpSession
     * @return
     */
    @PostMapping("loginout")
    public R<String> loginOut(HttpSession httpSession){
        httpSession.removeAttribute("user");
        return R.success("退出成功");
    }
}
