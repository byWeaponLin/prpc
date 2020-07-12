package com.github.weaponlin.cluster;

import com.github.weaponlin.client.PRequest;
import com.github.weaponlin.client.proxy.ClientHandler;
import com.github.weaponlin.codec.PDecoder;
import com.github.weaponlin.codec.PEncoder;
import com.github.weaponlin.exception.PRpcException;
import com.github.weaponlin.loadbalance.LoadBalancer;
import com.github.weaponlin.remote.URI;
import com.github.weaponlin.server.PResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class PAbstractCluster implements PCluster {

    private static final String FORMAT = "Request requestId: %s, serviceName: %s, methodName: %s, Response requestId: %s";

    private LoadBalancer loadBalancer;

    private PEncoder pEncoder;

    PAbstractCluster(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
        this.pEncoder = new PEncoder(PRequest.class);
    }

    Object doRequest(PRequest request) {
        // load balance
        final URI uri = loadBalancer.select(request.getServiceName());
        if (uri == null) {
            throw new PRpcException("can't select a server from load balancer");
        }
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        ClientHandler clientHandler = new ClientHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            // 这里添加解码器和编码器，防止拆包和粘包问题
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));

                            // 自定义序列化协议
                            pipeline.addLast(pEncoder);
                            pipeline.addLast(new PDecoder(PResponse.class));
                            // 添加自己的业务逻辑，将服务注册的handle添加到pipeline
                            pipeline.addLast(clientHandler);

                        }
                    });
            ChannelFuture future = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            future.channel().writeAndFlush(request).sync();
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("invoke service failed", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
        final PResponse response = (PResponse) clientHandler.getRes();
        if (response == null) {
            throw new PRpcException("response is null");
        } else if (response.getException() == null) {
            return response.getResult();
        } else {
            throw new PRpcException(getMessage(request, response), (Throwable) response.getException());
        }
    }

    private String getMessage(PRequest request, PResponse response) {
        return String.format(FORMAT, request.getRequestId(), request.getServiceName(),
                request.getMethodName(), response.getRequestId());
    }
}
