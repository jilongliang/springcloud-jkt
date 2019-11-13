package com.flong;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description OAuthApplication
 * @Date 2019/11/12 14:32
 * @Author liangjl
 * @Version V1.0
 * @Copyright (c) All Rights Reserved, 2019.
 */
@EnableFeignClients
@SpringCloudApplication
public class OAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuthApplication.class, args);
    }
}
