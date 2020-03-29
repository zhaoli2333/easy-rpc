package com.github.zhaoli.rpc.transport.tcp.codec;

import com.github.zhaoli.rpc.common.domain.Message;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhaoli on 2017/7/30.
 */
@Slf4j
public class TcpEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Message message = (Message) msg;
        out.writeInt(message.getHeader().getType());
        out.writeInt(message.getHeader().getVersion());
        out.writeInt(message.getHeader().getSerializerType());
        out.writeBytes(message.getBody());
    }


}
