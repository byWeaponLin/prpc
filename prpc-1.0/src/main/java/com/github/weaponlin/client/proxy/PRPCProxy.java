package com.github.weaponlin.client.proxy;


import com.github.weaponlin.client.PRequest;
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
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PRPCProxy implements InvocationHandler {

    private Class<?> klass;

    public PRPCProxy(Class<?> klass) {
        this.klass = klass;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("come in");

        final PRequest request = PRequest.builder()
                .serviceName(klass.getName())
                .methodName(method.getName())
                .params(args)
                .build();

        return sendRequest(request);
    }

    public Object sendRequest(PRequest request) {
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
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
        return clientNettyHandler.getRes();
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
