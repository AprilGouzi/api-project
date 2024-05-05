package com.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.SpringApplication;

/**
 * @author 囍崽
 * version 1.0
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@MapperScan("com.api.mapper")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
