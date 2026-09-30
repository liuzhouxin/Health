package com.health.warning;

import com.health.common.exception.GlobalExceptionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Import(GlobalExceptionHandler.class)
@EnableAsync
@EnableScheduling
@MapperScan("com.health.warning.mapper")
public class WarningApplication {
    public static void main(String[] args) {
        SpringApplication.run(WarningApplication.class, args);
    }
}