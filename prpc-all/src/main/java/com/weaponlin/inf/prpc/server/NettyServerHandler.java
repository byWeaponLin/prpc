package com.weaponlin.inf.prpc.server;

import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.protocol.prpc.PResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        PRequest request = (PRequest) msg;
        PResponse response = PResponse.builder()
                .requestId(request.getRequestId())
                .serviceName(request.getServiceName())
                .methodName(request.getMethodName())
                .build();
        try {
            Pair<Object, Method> instanceAndMethod = PInterface.getInstanceAndMethod(request.getGroup(),
                    request.getServiceName(), request.getMethodName(), request.getParameterTypes());

            log.info("receive request, request id: {}, service: {}, method: {}", request.getRequestId(),
                    request.getServiceName(), request.getMethodName());

            Method methodInstance = instanceAndMethod.getRight();
            Object serviceInstance = instanceAndMethod.getKey();
            Object result = methodInstance.invoke(serviceInstance, request.getParams());
            response.setResult(result);
            response.setResultType(result.getClass());
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
