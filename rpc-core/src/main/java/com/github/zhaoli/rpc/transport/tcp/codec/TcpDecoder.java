package com.github.zhaoli.rpc.transport.tcp.codec;

import com.github.zhaoli.rpc.common.domain.Header;
import com.github.zhaoli.rpc.common.domain.Message;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.enumeration.SerializerType;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.serialize.api.Serializer;
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
public class TcpDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int type = in.readInt();
        int version = in.readInt();
        int serializerType = in.readInt();

        Header header = Header.builder()
                .type(type)
                .version(version)
                .serializerType(serializerType)
                .build();

        byte[] body = new byte[in.readableBytes()];
        in.readBytes(body);

        out.add(Message.builder()
                .header(header)
                .body(body)
                .build());
    }
}
