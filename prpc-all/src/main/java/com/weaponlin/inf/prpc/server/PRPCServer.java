package com.weaponlin.inf.prpc.server;

import com.weaponlin.inf.prpc.codec.CodecPair;
import com.weaponlin.inf.prpc.codec.PCodec;
import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.loader.ServiceLoader;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import com.weaponlin.inf.prpc.registry.Registry;
import com.weaponlin.inf.prpc.registry.RegistryFactory;
import com.weaponlin.inf.prpc.utils.PortUtils;
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
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.weaponlin.inf.prpc.config.PRPConfig.PGroup;
import static com.weaponlin.inf.prpc.config.PRPConfig.PRegistryCenter;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

@Slf4j
public class PRPCServer {

    private PRPConfig config;

    private NettyServerHandler nettyServerHandler;

    /**
     * k: protocol, v: registry
     * -> k: registry
     * -> v: services
     */
    private Map<String, Map<PRegistryCenter, Map<String, List<PGroup>>>> protocolRegistryMap;

    private static List<Registry> registries = new ArrayList<>();


    static {
        // add shutdown hook to unregister service
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            registries.forEach(Registry::unregister);
        }));
    }

    public PRPCServer(@NonNull PRPConfig config) {
        this.config = config;
        this.nettyServerHandler = new NettyServerHandler();
        complementConfig();
        validateConfig();
    }

    private void complementConfig() {
        config.getGroups().forEach(group -> {
            if (group.getRegistryCenter() == null) {
                Optional.ofNullable(config.getRegistryCenter()).ifPresent(group::setRegistryCenter);
            }
            if (group.getConnectionTimeouts() <= 0) {
                Optional.ofNullable(config.getConnectionTimeout()).filter(e -> e <= 0)
                        .ifPresent(group::setConnectionTimeouts);
            }
            // codec从Extension里获取
            Set<String> codecSet = ServiceLoader.getServiceExtension(PCodec.class);
            if (StringUtils.isBlank(group.getCodec()) || !codecSet.contains(group.getCodec().toLowerCase())) {
                Optional.ofNullable(config.getCodec()).filter(StringUtils::isNotBlank)
                        .filter(codec -> codecSet.contains(codec.toLowerCase()))
                        .ifPresent(group::setCodec);
            }

            if (StringUtils.isBlank(group.getProtocol()) || !ProtocolType.contain(group.getProtocol())) {
                Optional.ofNullable(config.getProtocol()).filter(StringUtils::isNotBlank)
                        .filter(ProtocolType::contain)
                        .ifPresent(group::setProtocol);
            }

            if (StringUtils.isBlank(group.getGroup())) {
                Optional.ofNullable(config.getGroup()).filter(StringUtils::isNotBlank)
                        .ifPresent(group::setGroup);
            }

            if (StringUtils.isBlank(group.getBasePackage())) {
                throw new PRPCException("service base package is blank, please check it");
            }
        });

        // 聚合数据
        this.protocolRegistryMap = config.getGroups().stream().collect(groupingBy(PGroup::getProtocol,
                groupingBy(PGroup::getRegistryCenter,
                        groupingBy(PGroup::getCodec))));
    }

    public void start() {
        protocolRegistryMap.forEach((protocol, registryCenters) -> {
            ProtocolType protocolType = ProtocolType.getProtocolType(protocol);
            registryCenters.forEach((registryCenter, codecGroups) -> {
                codecGroups.forEach((codec, groups) -> {
                    int serverPort = PortUtils.getAvailablePort();
                    // 注册
                    Registry registry = RegistryFactory.createRegistry(registryCenter, groups, protocolType, serverPort);
                    registry.register();
                    registries.add(registry);

                    groups.forEach(group -> {
                        PInterface.registerInterface(group.getGroup(), group.getServices());
                        log.info("register instance success, group: {}, services: {}", group.getGroup(),
                                group.getServices().stream().map(Class::getName).collect(joining(",")));
                    });

                    log.info("start and register service success");
                    new Thread(() -> {
                        startServer(protocolType, codec, serverPort);
                    }).start();
                    log.info("start server success, server port: {}", serverPort);
                });
            });
        });
    }

    private void startServer(ProtocolType protocolType, String codec, int serverPort) {
        // 创建主从EventLoopGroup
        // 一个协议一个端口
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
                            CodecPair codecPair = CodecPair.getServerCodec(protocolType, codec);
                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            // 这里添加解码器和编码器，防止拆包和粘包问题
                            if (protocolType !=ProtocolType.dubbo) {
                                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                pipeline.addLast(new LengthFieldPrepender(4));
                            }

                            // 自定义序列化协议
                            pipeline.addLast(codecPair.getEncoder());
                            pipeline.addLast(codecPair.getDecoder());

                            // 添加自己的业务逻辑，将服务注册的handle添加到pipeline
                            pipeline.addLast(nettyServerHandler);
                        }
                    });
            // 这里同步等待future的返回，若返回失败，那么抛出异常
            ChannelFuture future = serverBootstrap.bind(serverPort).sync();
            // 关闭future
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("server start failed", e);
            throw new PRPCException("server start failed", e);
        } finally {
            // 最后记得主从group要优雅停机。
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void validateConfig() {
        // TODO
        List<PGroup> groups = config.getGroups();
        if (CollectionUtils.isEmpty(groups)) {
            throw new PRPCException("no identify service discovered");
        }
    }
}
