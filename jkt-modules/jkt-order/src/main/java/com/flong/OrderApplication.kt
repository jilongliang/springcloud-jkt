package com.flong

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient


@EnableDiscoveryClient
@SpringBootApplication
open class OrderApplication {

    //静态类
    companion object {
        /**启动SpringBoot的主入口.*/
        @JvmStatic fun main(args: Array<String>) {
            //*args的星号表示引用相同类型的变量
            SpringApplication.run(OrderApplication::class.java, *args)

        }
    }

}
