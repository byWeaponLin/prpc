package com.weaponlin.inf.prpc.server;

import com.weaponlin.inf.prpc.protocol.PPacket;
import com.weaponlin.inf.prpc.protocol.prpc.PMeta;
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
        PPacket packet = (PPacket) msg;

        PMeta meta = packet.getMeta();
        PResponse response = PResponse.builder()
                .requestId(meta.getRequestId())
                .serviceName(meta.getServiceName())
                .methodName(meta.getMethodName())
                .build();
        if (packet.isHeartbeat()) {
            ctx.writeAndFlush(response);
            ctx.close();
            return;
        }
        try {
            Pair<Object, Method> instanceAndMethod = PInterface.getInstanceAndMethod(meta.getServiceName(),
                    meta.getMethodName(), meta.getParameterTypes());

            log.info("receive request, request id: {}, service: {}, method: {}", meta.getRequestId(),
                    meta.getServiceName(), meta.getMethodName());

            Method methodInstance = instanceAndMethod.getRight();
            Object serviceInstance = instanceAndMethod.getKey();
            Object result = methodInstance.invoke(serviceInstance, meta.getParams());
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
