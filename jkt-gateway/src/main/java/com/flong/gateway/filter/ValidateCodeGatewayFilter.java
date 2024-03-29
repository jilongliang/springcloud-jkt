

package com.flong.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.flong.core.R;
import com.flong.core.constants.CommonConstants;
import com.flong.core.constants.SecurityConstants;
import com.flong.core.exception.ValidateCodeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flong.core.servlet.web.WebUtils;
import com.flong.gateway.config.FilterIgnorePropertiesConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class ValidateCodeGatewayFilter extends AbstractGatewayFilterFactory {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 不是登录请求，直接向下执行
            if (!StrUtil.containsAnyIgnoreCase(request.getURI().getPath()
                    , SecurityConstants.OAUTH_TOKEN_URL, SecurityConstants.SMS_TOKEN_URL)) {
                return chain.filter(exchange);
            }

            // 刷新token，直接向下执行
            String grantType = request.getQueryParams().getFirst("grant_type");
            if (StrUtil.equals(SecurityConstants.REFRESH_TOKEN, grantType)) {
                return chain.filter(exchange);
            }

            // 终端设置不校验， 直接向下执行
            try {
                String[] clientInfos = WebUtils.getClientId(request);
                if (filterIgnorePropertiesConfig.getClients().contains(clientInfos[0])) {
                    return chain.filter(exchange);
                }

                //校验验证码
                checkCode(request);
            } catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.PRECONDITION_REQUIRED);
                //设置response header，避免中文乱码
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
                try {
                    return response.writeWith(Mono.just(response.bufferFactory()
                            .wrap(objectMapper.writeValueAsBytes(
                                    R.builder().msg(e.getMessage())
                                            .code(CommonConstants.FAIL).build()))));
                } catch (JsonProcessingException e1) {
                    log.error("对象输出异常", e1);
                }
            }

            return chain.filter(exchange);
        };
    }

    /**
     * 检查code
     *
     * @param request
     * @throws ValidateCodeException 校验异常
     */
    private void checkCode(ServerHttpRequest request) throws ValidateCodeException {
        String code = request.getQueryParams().getFirst("code");

        if (StrUtil.isBlank(code)) {
            throw new ValidateCodeException("验证码不能为空");
        }

        String randomStr = request.getQueryParams().getFirst("randomStr");
        if (StrUtil.isBlank(randomStr)) {
            randomStr = request.getQueryParams().getFirst("mobile");
        }

        String key = CommonConstants.DEFAULT_CODE_KEY + randomStr;
        if (!redisTemplate.hasKey(key)) {
            throw new ValidateCodeException("验证码已过期");
        }

        Object codeObj = redisTemplate.opsForValue().get(key);

        if (codeObj == null) {
            redisTemplate.delete(key);
            throw new ValidateCodeException("验证码初始化异常");
        }

        String saveCode = codeObj.toString();
        if (StrUtil.isBlank(saveCode)) {
            redisTemplate.delete(key);
            throw new ValidateCodeException("验证码初始化异常");
        }

        if (!StrUtil.equalsIgnoreCase(saveCode, code)) {
            redisTemplate.delete(key);
            throw new ValidateCodeException("错误的验证码");
        }

        redisTemplate.delete(key);
    }
}
