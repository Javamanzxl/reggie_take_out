package com.zxl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxl.common.R;
import com.zxl.pojo.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {
    R<String> sendMsg(User user, HttpSession session);

    R<String> login(Map userMap, HttpSession session);
}
