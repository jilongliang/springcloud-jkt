package com.flong;

import com.flong.gateway.annotation.EnableDynamicRoute;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;


/**
 * @Description GateWayApplication
 * @Date 2019/11/11 15:09
 * @Author liangjl
 * @Version V1.0
 * @Copyright (c) All Rights Reserved, 2019.
 */
@EnableDynamicRoute
@SpringCloudApplication
public class GateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication.class, args);
    }
}
