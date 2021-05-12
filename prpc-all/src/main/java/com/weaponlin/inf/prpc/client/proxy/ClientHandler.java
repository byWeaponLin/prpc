package com.weaponlin.inf.prpc.client.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private Object res;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.res = msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("exception caught", cause);
    }

    public Object getRes() {
        return res;
    }
}
