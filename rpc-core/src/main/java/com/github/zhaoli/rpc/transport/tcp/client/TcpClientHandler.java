package com.github.zhaoli.rpc.transport.tcp.client;

import com.github.zhaoli.rpc.common.domain.MessageType;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.enumeration.SerializerType;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import com.github.zhaoli.rpc.transport.api.Client;
import com.github.zhaoli.rpc.common.domain.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;



/**
 * Created by zhaoli on 2017/7/31.
 */
@Slf4j
public class TcpClientHandler extends SimpleChannelInboundHandler<Message> {
    private Client client;
    public TcpClientHandler(Client client) {
        this.client = client;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("客户端捕获到异常");
        cause.printStackTrace();
        log.info("与服务器{} 的连接断开", client.getServiceURL().getAddress());
        client.handleException(cause);
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端与服务器{}通道已开启...", client.getServiceURL().getAddress());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) {
        if(message.getHeader().getType() == MessageType.PONG) {
            log.info("收到服务器的PONG心跳响应");
            return;
        }
        log.info("接收到服务器 {} 响应: {}", client.getServiceURL().getAddress(), message);
        int serializeType = message.getHeader().getSerializerType();
        Serializer serializer = SerializerType.getSerializerByType(serializeType);
        if(serializer == null) {
            throw new RPCException(ErrorEnum.UNSUPPOTED_SERIALIZE_TYPE, ErrorEnum.UNSUPPOTED_SERIALIZE_TYPE.getErrorCode());
        }
        RPCResponse response = serializer.deserialize(message.getBody(), RPCResponse.class);
        client.handleRPCResponse(response);
    }
}
