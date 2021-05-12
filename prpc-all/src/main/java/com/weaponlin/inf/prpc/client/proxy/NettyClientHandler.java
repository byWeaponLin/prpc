package com.weaponlin.inf.prpc.client.proxy;

import com.weaponlin.inf.prpc.protocol.prpc.PResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * TODO read response async
 * https://stackoverflow.com/questions/23128232/how-to-get-server-response-with-netty-client/35318079
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<PResponse> {

    private ChannelHandlerContext ctx;

    private BlockingQueue<Promise<String>> messageList = new ArrayBlockingQueue<>(16);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PResponse msg) throws Exception {

    }



}
