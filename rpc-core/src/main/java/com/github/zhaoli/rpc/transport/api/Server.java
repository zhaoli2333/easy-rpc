package com.github.zhaoli.rpc.transport.api;

import com.github.zhaoli.rpc.common.domain.RPCRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhaoli
 * @date 2018/7/19
 */
public interface Server {
    void run();

    void handleRPCRequest(RPCRequest request, ChannelHandlerContext ctx);

    void close();
}
