package com.github.zhaoli.rpc.transport.easy.codec;

import com.github.zhaoli.rpc.serialize.api.Serializer;
import com.github.zhaoli.rpc.common.domain.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhaoli on 2017/7/30.
 */
@Slf4j
public class EasyEncoder extends MessageToByteEncoder {
    private Serializer serializer;

    public EasyEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Message message = (Message) msg;
        out.writeByte((message.getType()));
        if (message.getType() == Message.REQUEST) {
            byte[] bytes = serializer.serialize(message.getRequest());
            log.info("Message:{},序列化大小为:{}", message,bytes.length);
            out.writeBytes(bytes);
            message.getRequest().recycle();
        } else if (message.getType() == Message.RESPONSE) {
            byte[] bytes = serializer.serialize(message.getResponse());
            log.info("Message:{},序列化大小为:{}", message,bytes.length);
            out.writeBytes(bytes);
            message.getResponse().recycle();
        }
    }
}
