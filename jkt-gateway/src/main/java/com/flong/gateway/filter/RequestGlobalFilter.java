

package com.flong.gateway.filter;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.flong.core.constants.CommonConstants;
import com.flong.core.R;
import com.flong.core.constants.SecurityConstants;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * <p>
 * 全局拦截器，作用所有的微服务
 * <p>
 * 1. 对请求头中参数进行处理 from 参数进行清洗
 * 2. 重写StripPrefix = 1,支持全局
 * <p>
 * 支持swagger添加X-Forwarded-Prefix header  （F SR2 已经支持，不需要自己维护）
 */
@Component
public class RequestGlobalFilter implements GlobalFilter, Ordered {
    private static final String HEADER_NAME = "X-Forwarded-Prefix";

    /**
     * Process the Web request and (optionally) delegate to the next
     * {@code WebFilter} through the given {@link GatewayFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 清洗请求头中from 参数
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove(SecurityConstants.FROM);
                })
                .build();

        // 2. 重写StripPrefix
        addOriginalRequestUrl(exchange, request.getURI());
        String rawPath = request.getURI().getRawPath();
        String newPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(rawPath, "/"))
                .skip(1L).collect(Collectors.joining("/"));
        ServerHttpRequest newRequest = request.mutate()
                .path(newPath)
                .build();
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
        // 处理undertow容器返回数据格式
        ServerHttpResponseDecorator decorator = produce(exchange);
        if (decorator != null) {
            return chain.filter(exchange.mutate()
                    .request(newRequest.mutate()
                            .build()).response(decorator).build());
        } else {
            return chain.filter(exchange.mutate()
                    .request(newRequest.mutate()
                            .build()).build());
        }

    }

    @Override
    public int getOrder() {
        return -1000;
    }

    private ServerHttpResponseDecorator produce(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpStatus status = this.getStatusCode();
                //处理500 undertow错误
                if (status == HttpStatus.INTERNAL_SERVER_ERROR && body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        // probably should reuse buffers
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        //释放掉内存
                        DataBufferUtils.release(dataBuffer);
                        String s = new String(content, Charset.forName("UTF-8"));
                        try {
                            JSONObject bodyJson = JSONObject.parseObject(s);
                            if (bodyJson.containsKey("error") && bodyJson.containsKey("message")) {
                                String regex = "UT[0-9]*:(.)*";
                                String matcherMsg = ReUtil.get(regex, bodyJson.getString("message"), 0);
                                R r = R.builder()
                                        .msg(StrUtil.isNotEmpty(matcherMsg) ? matcherMsg : bodyJson.getString("message"))
                                        .code(CommonConstants.FAIL)
                                        .build();
                                byte[] newContent = JSONObject.toJSONBytes(r, SerializerFeature.WriteMapNullValue);
                                return bufferFactory.wrap(newContent);
                            }
                        } catch (Exception e) {
                            //非JSON数据不进行处理
                        }
                        return bufferFactory.wrap(content);
                    }));
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
        return decoratedResponse;
    }
}
