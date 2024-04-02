package com.sp.orange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j //lombok提供的日志控制
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement//开启事务控制
public class orange {
    public static void main(String[] args) {
        SpringApplication.run(orange.class,args);
        log.info("项目启动成功 ...");
    }
}
