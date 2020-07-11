package com.github.weaponlin.server;

import com.github.weaponlin.client.PRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static Map<String, Object> cachedInstances = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PRequest request = (PRequest) msg;
        PResponse response = PResponse.builder()
                .requestId(request.getRequestId())
                .build();
        try {
            Object object = cachedInstances.get(request.getServiceName());
            if (object == null) {
                Reflections reflections = new Reflections(request.getServiceName());
                final Class<?> apiClass = Class.forName(request.getServiceName());
                final Set<Class<?>> subTypes = reflections.getSubTypesOf((Class<Object>) apiClass);
                final Class<?> implementationClass = (Class<?>) subTypes.toArray()[0];
                object = implementationClass.getConstructor().newInstance();
                cachedInstances.put(request.getServiceName(), object);
            }

            log.info("receive request, request id: {}", request.getRequestId());
            Object result = MethodUtils.invokeMethod(object, request.getMethodName(), request.getParams());
            response.setResult(result);
        } catch (Exception e) {
            log.error("invoke service implement failed", e);
            response.setException(e);
        }
        ctx.writeAndFlush(response);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
