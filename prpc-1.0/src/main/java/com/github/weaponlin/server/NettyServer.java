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
import org.apache.commons.lang3.reflect.MethodUtils;
import org.reflections.Reflections;

import java.util.Set;

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

    public class NettyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            PRequest request = (PRequest) msg;

            Reflections reflections = new Reflections(request.getServiceName());
            final Class<?> apiClass = Class.forName(request.getServiceName());
            final Set<Class<?>> subTypes = reflections.getSubTypesOf((Class<Object>) apiClass);
            final Class<?> implementationClass = (Class<?>) subTypes.toArray()[0];
            final Object object = implementationClass.getConstructor().newInstance();

            Object response = MethodUtils.invokeMethod(object, request.getMethodName(), request.getParams());

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
