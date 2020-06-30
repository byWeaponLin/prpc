package com.github.weaponlin.client.proxy;


import com.github.weaponlin.client.PRequest;
import com.github.weaponlin.exception.PException;
import com.github.weaponlin.server.PResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
public class PRPCProxy implements InvocationHandler {

    private static final String FORMAT = "Request requestId: %s, serviceName: %s, methodName: %s, Response requestId: %s";

    private Class<?> klass;

    public PRPCProxy(Class<?> klass) {
        this.klass = klass;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final PRequest request = PRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .serviceName(klass.getName())
                .methodName(method.getName())
                .params(args)
                .build();
        // TODO fail strategy, eg: failover, failfast, failback, failsafe
        return sendRequest(request);
    }

    private Object sendRequest(PRequest request) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        ClientNettyHandler clientNettyHandler = new ClientNettyHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 这里添加解码器和编码器，防止拆包和粘包问题
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));

                            // 这里采用jdk的序列化机制
                            pipeline.addLast("jdkencoder", new ObjectEncoder());
                            pipeline.addLast("jdkdecoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            // 添加自己的业务逻辑，将服务注册的handle添加到pipeline
                            pipeline.addLast(clientNettyHandler);

                        }
                    });

            ChannelFuture future = bootstrap.connect("127.0.0.1", 8888).sync();
            future.channel().writeAndFlush(request).sync();
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("invoke service failed", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
        final PResponse response = (PResponse) clientNettyHandler.getRes();
        if (response == null) {
            // TODO response is null if response is too large
            throw new PException("response is null");
        } else if (response.getException() == null) {
            return response.getResult();
        } else {
            throw new PException(getMessage(request, response), (Throwable) response.getException());
        }
    }

    private String getMessage(PRequest request, PResponse response) {
        return String.format(FORMAT, request.getRequestId(), request.getServiceName(),
                request.getMethodName(), response.getRequestId());
    }

    private static class ClientNettyHandler extends ChannelInboundHandlerAdapter {
        private Object res;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            this.res = msg;
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }

        public Object getRes() {
            return res;
        }
    }
}
