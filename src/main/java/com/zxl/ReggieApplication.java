package com.zxl;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@MapperScan("com.zxl.mapper")
@EnableTransactionManagement
@EnableCaching //开启springCache注解
//@ServletComponentScan
public class ReggieApplication {
    public static void main(String[] args) {

        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动");
    }



}