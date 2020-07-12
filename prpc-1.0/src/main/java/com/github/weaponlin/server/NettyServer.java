package com.github.weaponlin.server;

import com.github.weaponlin.client.PRequest;
import com.github.weaponlin.codec.PDecoder;
import com.github.weaponlin.codec.PEncoder;
import com.github.weaponlin.config.PRPCConfig;
import com.github.weaponlin.exception.PRpcException;
import com.github.weaponlin.registry.Registry;
import com.github.weaponlin.registry.ZooKeeperRegistry;
import com.github.weaponlin.utils.NetUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.github.weaponlin.config.PRPCConfig.*;

@Slf4j
public class NettyServer {

    private int port;

    private PEncoder pEncoder;

    private NettyServerHandler nettyServerHandler;

    /**
     * temporary
     */
    private RegistryProperties registryProperties;

    private List<Class<?>> serviceList;

    private Registry registry;

    public NettyServer(int port, RegistryProperties registryProperties) {
        this(port, registryProperties, null);
    }

    public NettyServer(int port, RegistryProperties registryProperties, List<Class<?>> serviceList) {
        this.port = port;
        this.pEncoder = new PEncoder(PResponse.class);
        this.nettyServerHandler = new NettyServerHandler();
        this.registryProperties = registryProperties;
        this.serviceList = serviceList;
        this.registry = new ZooKeeperRegistry(port, serviceList, registryProperties);
    }

    public void start() {
        registerService();
        startService();
        log.info("server start successfully, host: {}, port: {}", NetUtils.getLocalHost(), port);
    }

    /**
     * TODO register service
     */
    private void registerService() {
        registry.register();
    }

    /**
     * start service
     */
    private void startService() {
        // 创建主从EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 将主从主从EventLoopGroup绑定到server上
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            // 这里添加解码器和编码器，防止拆包和粘包问题
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));

                            // 自定义序列化协议
                            pipeline.addLast(pEncoder);
                            pipeline.addLast(new PDecoder(PRequest.class));

                            // 添加自己的业务逻辑，将服务注册的handle添加到pipeline
                            pipeline.addLast(nettyServerHandler);
                        }
                    });
            // 这里同步等待future的返回，若返回失败，那么抛出异常
            ChannelFuture future = serverBootstrap.bind(port).sync();
            // 关闭future
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("server start failed", e);
            throw new PRpcException("server start failed", e);
        } finally {
            // 最后记得主从group要优雅停机。
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
