package com.github.weaponlin.server;

import com.github.weaponlin.client.PRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NettyServer {

    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() {
        // 创建主从EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 将主从主从EventLoopGroup绑定到server上
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
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
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            // 这里同步等待future的返回，若返回失败，那么抛出异常
            ChannelFuture future = serverBootstrap.bind(port).sync();
            // 关闭future
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 最后记得主从group要优雅停机。
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Slf4j
    public static class NettyServerHandler extends ChannelInboundHandlerAdapter {

        private Map<String, Object> cachedInstances = new ConcurrentHashMap<>();

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
}
