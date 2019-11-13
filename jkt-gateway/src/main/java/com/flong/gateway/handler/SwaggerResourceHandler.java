

package com.flong.gateway.handler;

import com.flong.core.constants.CommonConstants;
import com.flong.gateway.vo.RouteDefinitionVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * SwaggerResourceHandler
 */
@Slf4j
@Component
@AllArgsConstructor
public class SwaggerResourceHandler implements HandlerFunction<ServerResponse> {
    private final SwaggerResourcesProvider swaggerResources;
    private final RedisTemplate redisTemplate;

    /**
     * Handle the given request.
     *
     * @param request the request to handler
     * @return the response
     */
    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        List<SwaggerResource> list = swaggerResources.get();
        List<SwaggerResource> newlist = new ArrayList<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(RouteDefinitionVo.class));
        List<RouteDefinitionVo> values = redisTemplate.opsForHash().values(CommonConstants.ROUTE_KEY);
        if (null != values && !values.isEmpty()) {
            Collections.sort(values, new Comparator<RouteDefinitionVo>() {
                @Override
                public int compare(RouteDefinitionVo arg0, RouteDefinitionVo arg1) {
                    int hits0 = arg0.getOrder();
                    int hits1 = arg1.getOrder();
                    if (hits1 > hits0) {
                        return 1;
                    } else if (hits1 == hits0) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            values.forEach(v -> {
                list.forEach(old -> {
                    if (v.getId().equals(old.getName())) {
                        newlist.add(old);
                    }
                });
            });
        }
        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(newlist));
    }
}
