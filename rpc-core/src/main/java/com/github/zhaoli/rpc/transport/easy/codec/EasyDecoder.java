package com.github.zhaoli.rpc.transport.easy.codec;

import com.github.zhaoli.rpc.serialize.api.Serializer;
import com.github.zhaoli.rpc.common.domain.Message;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by zhaoli on 2017/7/30.
 * 
 * 
 */
@Slf4j
public class EasyDecoder extends ByteToMessageDecoder {
    private Serializer serializer;

    public EasyDecoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();
        if (type == Message.PING) {
            out.add(Message.PING_MSG);
        } else if (type == Message.PONG) {
            out.add(Message.PONG_MSG);
        } else {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            if (type == Message.REQUEST) {
                out.add(Message.buildRequest(serializer.deserialize(bytes, RPCRequest.class)));
            } else if (type == Message.RESPONSE) {
                out.add(Message.buildResponse(serializer.deserialize(bytes, RPCResponse.class)));
            }
        }
    }
}
