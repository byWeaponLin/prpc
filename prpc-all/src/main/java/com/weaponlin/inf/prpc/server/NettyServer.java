package com.weaponlin.inf.prpc.server;

import com.weaponlin.inf.prpc.annotation.PRPC;
import com.weaponlin.inf.prpc.client.PClient;
import com.weaponlin.inf.prpc.client.PRequest;
import com.weaponlin.inf.prpc.codec.PDecoder;
import com.weaponlin.inf.prpc.codec.PEncoder;
import com.weaponlin.inf.prpc.config.PConfig;
import com.weaponlin.inf.prpc.exception.PRpcException;
import com.weaponlin.inf.prpc.registry.Registry;
import com.weaponlin.inf.prpc.registry.RegistryFactory;
import com.weaponlin.inf.prpc.utils.NetUtils;
import com.weaponlin.inf.prpc.utils.PortUtils;
import com.google.common.collect.Lists;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyServer {

    private int port;

    private PEncoder pEncoder;

    private NettyServerHandler nettyServerHandler;

    private Map<String, PClient.GroupRegistry> groupRegistry = new ConcurrentHashMap<>();

    private Map<String, Registry> registryMap = new ConcurrentHashMap<>();

    private PConfig config;

    private List<Class<?>> services = Lists.newArrayList();


    private static List<Class<?>> servicesBak;
    private static Map<String, Registry> registryMapBak;
    private static Map<String, PClient.GroupRegistry> groupRegistryBak;

    static {
        // TODO may be refactor
        // add shutdown hook to unregister service
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            servicesBak.forEach(service -> {
                final PRPC prpc = service.getAnnotation(PRPC.class);
                String group = prpc.group();
                Registry registry = registryMapBak.get(groupRegistryBak.get(group).getAddress());
                registry.unregister();
            });
        }));
    }

    /**
     * TODO remove port from constructor
     * @param port
     * @param config
     */
    @Deprecated
    public NettyServer(int port, PConfig config) {
        this.port = port;
        configValidate(config);
        this.config = config;
        this.pEncoder = new PEncoder(PResponse.class, config.getCodec());
        this.nettyServerHandler = new NettyServerHandler();
        servicesBak = services;
        registryMapBak = registryMap;
        groupRegistryBak = groupRegistry;
    }

    public NettyServer(PConfig config) {
        this(PortUtils.getAvailablePort(), config);
    }

    public NettyServer addService(Class<?> service) {
        final PRPC prpc = service.getAnnotation(PRPC.class);
        if (prpc == null || StringUtils.isBlank(prpc.group())) {
            throw new PRpcException("class must annotate with @PRPC or group cant be blank");
        }
        final String group = prpc.group();
        if (!groupRegistry.containsKey(group) && StringUtils.isBlank(config.getAddress())) {
            throw new PRpcException("cant find zookeeper for group " + group);
        }
        groupRegistry.putIfAbsent(group, new PClient.GroupRegistry(config.getAddress()));
        final PClient.GroupRegistry groupRegistry = this.groupRegistry.get(group);
        if (!registryMap.containsKey(groupRegistry.getAddress())) {
            Registry registry = RegistryFactory.createRegistry(config, port);
            registryMap.putIfAbsent(groupRegistry.getAddress(), registry);
        }
        PInterface.registerInterface(group, service);
        services.add(service);
        return this;
    }

    public NettyServer addService(List<Class<?>> services) {
        Optional.ofNullable(services).filter(CollectionUtils::isNotEmpty)
                .ifPresent(list -> list.forEach(this::addService));
        return this;
    }

    public synchronized void start() {
        registerService();
        startService();
        log.info("server start successfully, host: {}, port: {}", NetUtils.getLocalHost(), port);
    }

    private void registerService() {
        services.forEach(service -> {
            final PRPC prpc = service.getAnnotation(PRPC.class);
            String group = prpc.group();
            Registry registry = registryMap.get(groupRegistry.get(group).getAddress());
            registry.register(service);
        });
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
//                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            // 这里添加解码器和编码器，防止拆包和粘包问题
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));

                            // 自定义序列化协议
                            pipeline.addLast(pEncoder);
                            // TODO get protocolType from configuration
                            pipeline.addLast(new PDecoder(PRequest.class, "protobuf"));

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

    private void configValidate(PConfig config) {
        if (config == null) {
            throw new PRpcException("config cant be null");
        }
        // TODO config validate by registry
        if (StringUtils.isBlank(config.getAddress())) {

            if (CollectionUtils.isEmpty(config.getGroups())) {
                throw new PRpcException("no valid zookeeper configuration");
            }

            config.getGroups().stream().filter(Objects::nonNull).forEach(group -> {
                Optional.ofNullable(group.getGroup()).filter(StringUtils::isNotBlank)
                        .orElseThrow(() -> new PRpcException("group is invalid for it is blank"));
                Optional.ofNullable(group.getAddress()).filter(StringUtils::isNotBlank)
                        .orElseThrow(() -> new PRpcException("invalid zookeeper configuration"));
                groupRegistry.putIfAbsent(group.getGroup(), new PClient.GroupRegistry(group.getAddress()));
            });
        } else {
            if (CollectionUtils.isEmpty(config.getGroups())) {
                return;
            }
            config.getGroups().stream().filter(Objects::nonNull).forEach(group -> {
                Optional.ofNullable(group.getGroup()).filter(StringUtils::isNotBlank)
                        .orElseThrow(() -> new PRpcException("group is invalid for it is blank"));
                if (StringUtils.isNotBlank(group.getAddress())) {
                    groupRegistry.putIfAbsent(group.getGroup(), new PClient.GroupRegistry(group.getAddress()));
                } else {
                    groupRegistry.putIfAbsent(group.getGroup(), new PClient.GroupRegistry(config.getAddress()));
                }
            });
        }
    }
}
