package com.zxl.common;

/**
 * @author ：xxx
 * @description：TODO 基于ThreadLocal封装的工具类，用于保存和获取当前登录用户的id
 * @date ：2024/02/02 17:09
 */
public class BaseContext {
    private static ThreadLocal threadLocal = new ThreadLocal<Long>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return (Long) threadLocal.get();
    }
}
