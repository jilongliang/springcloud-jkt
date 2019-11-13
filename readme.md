# 1、框架結構
* kotlin + java + springcloud + docker + maven


# 2、參考基礎架構
* [Kotlin+SpringBoot+MyBatis完美搭建最简洁最酷的前后端分离框架](https://www.jianshu.com/p/0acd593fd11e)
* [Kotlin+SpringBoot与Minio存储整合详解](https://www.jianshu.com/p/aabbf8d74540)
* [Kotlin+SpringBoot与RabbitMQ整合详解](https://www.jianshu.com/p/41b3cca5b2a7)
* 


# 3、啟動順序
* 其他工程暫無啟動順序
```
1、EuerkaApplication
2、ConfigApplication
3、GateWayApplication
4、OAuthApplication
```


# 4、端口分配

工程术语 | 工程描述|工程端口
---|---|---
EuerkaApplication| 注册中心|8761
ConfigApplication| 配置中心|8001
GateWayApplication|网关工程|8002
OAuthApplication|授权工程|8003
UserApplication|用户工程|7000
OrderApplication|订单工程|7001

# 5、脚本
```
compile-update-code.sh 更新和编译代码
deploy-application.sh  发布应用工程
```



# 6、解決網關 Error starting Tomcat context. Exception
* 解決方法，去掉`spring-boot-starter-web
* 參考文章[Spring Cloud 的 Gateway 服务启动报错](https://blog.csdn.net/weixin_41922349/article/details/91560886)
```
<dependency> 
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
 </dependency> 
```

