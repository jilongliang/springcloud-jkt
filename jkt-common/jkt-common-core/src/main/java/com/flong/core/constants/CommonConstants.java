package com.flong.core.constants;

/**
 * @Description CommonConstants
 * @Date 2019/11/11 14:33
 * @Author liangjl
 * @Version V1.0
 * @Copyright (c) All Rights Reserved, 2019.
 */

public interface CommonConstants {


    /**
     * @Descript   数据不存在的提示
     * @Date	   2019/7/26 15:19
     * @Author	   liangjl
     */
    String DATA_NOT_EXIST 	= "当前操作的数据不存在!";


    /**
     * header 中租户ID
     */
    String TENANT_ID = "TENANT_ID";
    /**
     * 删除
     */
    String STATUS_DEL = "1";
    /**
     * 正常
     */
    String STATUS_NORMAL = "0";

    /**
     * 锁定
     */
    String STATUS_LOCK = "9";

    /**
     * 菜单
     */
    String MENU = "0";

    /**
     * 编码
     */
    String UTF8 = "UTF-8";


    /**
     * 路由存放
     */
    String ROUTE_KEY = "gateway_route_key";

    /**
     * spring boot admin 事件key
     */
    String EVENT_KEY = "event_key";

    /**
     * 验证码前缀
     */
    String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY_";

    /**
     * 成功标记
     */
    Integer SUCCESS = 0;
    /**
     * 失败标记
     */
    Integer FAIL = 1;


    /**
     * 错误码Redis键
     */
    String SYS_ERROR_KEY = "system_error_key";


}