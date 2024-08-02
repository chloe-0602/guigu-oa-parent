package com.chloe;

import com.chloe.common.exception.GlobalExceptionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.chloe"})
@MapperScan(basePackages = {"com.chloe.process.mapper", "com.chloe.auth.mapper", "com.chloe.wechat.mapper"})
@Import(GlobalExceptionHandler.class)
public class ServiceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}
