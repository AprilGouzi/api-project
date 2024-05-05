package com.api.filter;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.api.model.entity.InterfaceInfo;
import com.api.model.entity.User;
import com.api.service.InnerInterfaceInfoService;
import com.api.service.InnerUserInterfaceInfoService;
import com.api.service.InnerUserService;
import com.api.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author 囍崽
 * version 1.0
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService interfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1", "127.0.0.2");

    private static final String INTERFACE_HOST = "http://localhost:8090";

    //1、用户发送请求到API网关
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //log.info("custom global filter");
        //2、请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = Objects.requireNonNull(request.getMethod().toString());

        log.info("请求id:{}", request.getId());
        log.info("请求路径:{}", path);
        log.info("请求方法:{}", method);
        log.info("请求参数：{}", request.getQueryParams());
        log.info("请求头:{}", request.getHeaders());
        String remoteAddress = request.getRemoteAddress().getHostString();
        log.info("请求地址：{}", remoteAddress);

        ServerHttpResponse response = exchange.getResponse();
        //3、访问控制-黑白名单
        if (!IP_WHITE_LIST.contains(remoteAddress)) {
            handleNoAuth(response);
        }

        //4、用户鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        //防止中文乱码
        String body = null;
        try {
            body = URLDecoder.decode(headers.getFirst("body"), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String sign = headers.getFirst("sign");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        boolean hasBlank = StrUtil.hasBlank(accessKey, body, sign, nonce, timestamp);
        if (hasBlank) {
            return handleInvokeError(response);
        }

        // TODO 实际情况是到数据库钟查询 secretKey
        //使用accessKey查询secretKey
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleInvokeError(response);
        }
        String secretKey = invokeUser.getSecretKey();
        String sign1 = SignUtil.getSign(body, secretKey);
        if (!StrUtil.equals(sign, sign1)) {
            return handleInvokeError(response);
        }


        //if (!"api".equals(accessKey)) {
        //    return handleNoAuth(response);
        //}
        //String serverSign = SignUtil.getSign(body, "abcdefgh");
        //if (!sign.equals(serverSign)) {
        //    throw new RuntimeException("无权限");
        //}

        //if (Long.parseLong(nonce) > 10000L) {
        //    return handleNoAuth(response);
        //}
        // TODO 时间和当前时间不能超过5分钟
        // 时间戳是否为数字
        if (!NumberUtil.isNumber(timestamp)) {
            return handleInvokeError(response);
        }
        Long currentTime = System.currentTimeMillis() / 1000;
        Long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleInvokeError(response);
        }

        //5、请求的模拟接口是否存在
        // TODO 从数据库钟查询模拟接口是否存在，以及请求方法是否匹配（还可以校验请求参数）
        InterfaceInfo invokeInterfaceInfo = null;
        try {
            invokeInterfaceInfo = interfaceInfoService.getInvokeInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInvokeInterfaceInfo error", e);
        }
        if (invokeInterfaceInfo == null) {
            return handleInvokeError(response);
        }

        //是否有调用次数
        if (!innerUserInterfaceInfoService.hasInvokeNum(invokeUser.getId(),
                invokeInterfaceInfo.getId())) {
            return handleInvokeError(response);
        }

        //6、请求转发，调用的模拟接口
        //Mono<Void> filter = chain.filter(exchange);
        //7、响应日志
        log.info("响应状态码：{}", response.getStatusCode());
        //6、请求转发，调用的模拟接口
        return handleResponse(exchange, chain, invokeUser.getId(), invokeInterfaceInfo.getId());

        //if (response.getStatusCode() == HttpStatus.OK) {
        //    // 调用次数+1
        //    //8、调用成功，接口调用次数+1
        //} else {
        //    //9、调用失败，返回规范错误码
        //    return handleInvokeError(response);
        //}
        //return filter;
    }


    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long userId, long interfaceInfoId) {
        try {
            //从交换机拿到原始response
            ServerHttpResponse originalResponse = exchange.getResponse();
            //缓冲区工厂，难道缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //拿到状态码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                //增强能力，装饰者模式
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux:{}", (body instanceof Flux));
                        //对象是响应
                        if (body instanceof Flux) {
                            //拿到真正的body
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //往返回值里写数据
                            //拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // TODO 调用成功，接口调用次数 + 1
                                try {
                                    innerUserInterfaceInfoService.invokeInterfaceCount(userId, interfaceInfoId);
                                } catch (Exception e) {
                                    log.error("invokeInterfaceInfo error", e);
                                }

                                //data从这个content中读取
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放内存

                                //构建日志
                                MediaType contentType = originalResponse.getHeaders().getContentType();
                                if (!MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                                    return bufferFactory.wrap(content);
                                }

                                // 构建返回日志
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                String data = new String(content, StandardCharsets.UTF_8);
                                rspArgs.add(data);
                                //打印日志
                                log.info("<-- status:{} data:{}", rspArgs.toArray());

                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<-- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                //设置 response 响应对象为装饰过的对象
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}
