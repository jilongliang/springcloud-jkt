

package com.flong.gateway.handler;

import cn.hutool.core.util.StrUtil;
import com.flong.core.constants.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.flong.core.R;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;

/**
 * Hystrix 降级处理
 */
@Slf4j
@Component
public class HystrixFallbackHandler implements HandlerFunction<ServerResponse> {
    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        Optional<Object> originalUris = serverRequest.attribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        Map<String, String> results = new HashMap<>();
        originalUris.ifPresent(originalUri -> {
            log.error("网关执行请求:{}失败,hystrix服务降级处理", originalUri);
            results.put("serviceName", StrUtil.subBetween(StrUtil.str(originalUri, "UTF-8"), "lb://", "/"));
        });

        log.info("网关执行请求[originalUris]:{}", originalUris);

        String msg = String.format("服务%s异常，请检查服务是否启动且正常运行", results.get("serviceName") == null ? "" : results.get("serviceName"));

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(R.builder()
                        .msg(msg)
                        .code(CommonConstants.FAIL)
                        .build()));
    }

}
