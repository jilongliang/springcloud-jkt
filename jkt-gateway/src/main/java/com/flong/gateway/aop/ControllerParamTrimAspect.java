package com.flong.gateway.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Iterator;
import java.util.Map;

/**
 * 控制器字符串参数处理
 */
@Component
@Slf4j
@Aspect
public class ControllerParamTrimAspect {

    @Around("@annotation(requestMapping)")
    public Object aroundRequestMapping(ProceedingJoinPoint point, RequestMapping requestMapping) throws Throwable {
        return getObject(point);
    }

    @Around("@annotation(getMapping)")
    public Object aroundGetMapping(ProceedingJoinPoint point, GetMapping getMapping) throws Throwable {
        return getObject(point);
    }

    @Around("@annotation(postMapping)")
    public Object aroundPostMapping(ProceedingJoinPoint point, PostMapping postMapping) throws Throwable {
        return getObject(point);
    }

    @Around("@annotation(deleteMapping)")
    public Object aroundDeleteMapping(ProceedingJoinPoint point, DeleteMapping deleteMapping) throws Throwable {
        return getObject(point);
    }

    @Around("@annotation(putMapping)")
    public Object aroundPutMapping(ProceedingJoinPoint point, PutMapping putMapping) throws Throwable {
        return getObject(point);
    }

    private Object getObject(ProceedingJoinPoint point) throws Throwable {
        String strClassName = point.getTarget().getClass().getName();
        String strMethodName = point.getSignature().getName();
        log.debug("拦截请求参数：{}.{}", strClassName, strMethodName);
        Object[] args = point.getArgs();
        if (args == null || args.length == 0) {
            point.proceed();
        }
        try {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof String && arg != null) {
                    args[i] = StrUtil.trim(StrUtil.toString(arg));
                } else {
                    //非字符串对象，处理其字符串字段
                    Map<String, Object> beanMap = BeanUtil.beanToMap(arg);
                    Iterator<String> it = beanMap.keySet().iterator();
                    while (it.hasNext()) {
                        String fieldName = it.next();
                        Object value = beanMap.get(fieldName);
                        if (value instanceof String && value != null) {
                            BeanUtil.setFieldValue(arg, fieldName, StrUtil.toString(value).trim());
                        }
                    }
                    args[i] = arg;
                }
            }
        } catch (Exception e) {
            point.proceed();
        }
        return point.proceed(args);
    }

}
