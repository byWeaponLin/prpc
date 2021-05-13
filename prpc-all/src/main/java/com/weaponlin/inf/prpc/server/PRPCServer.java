package com.weaponlin.inf.prpc.server;

import com.weaponlin.inf.prpc.codec.CodecPair;
import com.weaponlin.inf.prpc.codec.CodecType;
import com.weaponlin.inf.prpc.codec.PDecoder;
import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.registry.Registry;
import com.weaponlin.inf.prpc.registry.RegistryFactory;
import com.weaponlin.inf.prpc.registry.RegistryNaming;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.weaponlin.inf.prpc.config.PRPConfig.PGroup;
import static com.weaponlin.inf.prpc.config.PRPConfig.PRegistry;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
public class PRPCServer {

    private PRPConfig config;

    private NettyServerHandler nettyServerHandler;

    Map<String, Map<PRegistry, List<PGroup>>> protocolRegistryMap;

    public PRPCServer(@NonNull PRPConfig config) {
        this.config = config;
        this.nettyServerHandler = new NettyServerHandler();
        complementConfig();
        validateConfig();
    }

    private void complementConfig() {
        config.getGroups().forEach(group -> {
            if (group.getRegistry() == null) {
                Optional.ofNullable(config.getRegistry()).ifPresent(group::setRegistry);
            }
            if (group.getConnectionTimeouts() <= 0) {
                Optional.ofNullable(config.getConnectionTimeout()).filter(e -> e <= 0)
                        .ifPresent(group::setConnectionTimeouts);
            }
            if (StringUtils.isBlank(group.getCodec()) || !CodecType.contain(group.getCodec())) {
                Optional.ofNullable(config.getCodec()).filter(StringUtils::isNotBlank)
                        .filter(CodecType::contain)
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

            if (CollectionUtils.isEmpty(group.getServices())) {
                throw new PRPCException("no identify service discovered");
            }
        });
    }


    public void registerService() {
        this.protocolRegistryMap = config.getGroups().stream()
                .collect(groupingBy(PGroup::getProtocol, groupingBy(PGroup::getRegistry)));

        // TODO

    }


    public void start() {
        // TODO
        registerService();
        protocolRegistryMap.entrySet().forEach(entry -> {
            Map<PRegistry, List<PGroup>> registryMap = entry.getValue();
            ProtocolType protocolType = ProtocolType.getProtocolType(entry.getKey());
            // 暂时写死
            CodecPair codecPair = CodecPair.getServerCodec(protocolType, "protobuf");
            int port = PortUtils.getAvailablePort();

            // 先启动服务，再注册service到注册中心
            startService(codecPair, port);

            // 注册
            registryMap.forEach((registry, groups) -> {
                Registry registry1 = RegistryFactory.createRegistry(registry.getNaming(), registry.getAddress(), port);

                groups.stream().map(PGroup::getServices).filter(CollectionUtils::isNotEmpty)
                        .flatMap(Collection::stream).forEach(service -> {
                            registry1.register(service);
                });
            });
        });
    }

    private void startService(CodecPair codecPair, int port) {
        // 创建主从EventLoopGroup
        // TODO 一个协议一个端口
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
                            pipeline.addLast(codecPair.getEncoder());
                            // TODO get protocolType from configuration
                            pipeline.addLast(codecPair.getDecoder());

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
            throw new PRPCException("server start failed", e);
        } finally {
            // 最后记得主从group要优雅停机。
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void validateConfig() {
        // TODO
        List<PGroup> groups = config.getGroups();
        if (CollectionUtils.isEmpty(groups)) {
            throw new PRPCException("no identify service discovered");
        }

        groups.forEach(group -> {
            PRegistry registry = group.getRegistry();
            if (registry != null && (!RegistryNaming.contain(registry.getNaming())
                    || StringUtils.isNotBlank(registry.getAddress())) ) {
                throw new PRPCException("invalid registry: " + registry + "group: ");
            }
        });
    }
}
