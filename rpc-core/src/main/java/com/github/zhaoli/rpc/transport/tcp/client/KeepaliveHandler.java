package com.github.zhaoli.rpc.transport.tcp.client;

import com.github.zhaoli.rpc.common.domain.Message;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import io.netty.channel.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class KeepaliveHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            log.info("write idle happen. so need to send keepalive to keep connection not closed by server");
            ctx.writeAndFlush(Message.PING).addListener((ChannelFutureListener) channelFuture -> {
                // 处理发送失败的情况
                if (!channelFuture.isSuccess()) {
                    log.error("发送心跳消息失败");
                }
            });
        }
        super.userEventTriggered(ctx, evt);
    }

}
