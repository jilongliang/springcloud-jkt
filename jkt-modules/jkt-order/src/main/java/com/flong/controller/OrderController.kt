package com.flong.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/order")
open class OrderController{


    @RequestMapping("/list1")
    fun list1():  String{
        return "NewFile" //跳转页面
    }

}