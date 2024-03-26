package com.zxl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.common.BaseContext;
import com.zxl.common.R;
import com.zxl.mapper.UserMapper;
import com.zxl.pojo.User;
import com.zxl.service.UserService;
import com.zxl.utils.SMSUtils;
import com.zxl.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/05 10:01
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public R<String> sendMsg(User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if(!StringUtils.isNotEmpty(phone)){
            return R.error("手机号为空");
        }
        //生成随机的4为验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("code={}",code);
            //调用阿里云提供的短信服务API完成发送短信
                //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
        //需要将生成的验证码保存到Session中
        session.setAttribute(phone,code);
        //将生成的验证码缓存到Redis中,并且设置有效期为5分钟
        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
        return R.success("手机验证码短信发送成功");
    }

    @Override
    public R<String> login(Map userMap, HttpSession session) {
        //获取手机号
        String phone = userMap.get("phone").toString();
        //获取验证码
        String code = userMap.get("code").toString();
        //从Session中获取存储的验证码
        Object codeInSession = session.getAttribute(phone);
        //从Redis中取出缓存的验证码
            Object codeInRedis = redisTemplate.opsForValue().get(phone);
        //验证码判断
        if(code!=null && code.equals(codeInRedis)){
            //验证码严重通过
            //判断该手机号是否被注册，如果没有注册进行新用户注册
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userMapper.selectOne(userLambdaQueryWrapper);
            //新用户注册
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userMapper.insert(user);
            }
            session.setAttribute("user",user.getId());
            //用户登录成功，删除Redis中缓存的验证码
            redisTemplate.delete(phone);
            return R.success("登陆成功");
        }

        return R.error("登录失败");
    }
}
