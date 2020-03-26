package com.github.zhaoli.rpc.transport.tcp.client;

import com.github.zhaoli.rpc.transport.tcp.constant.TcpConstant;
import com.github.zhaoli.rpc.transport.api.constant.FrameConstant;
import com.github.zhaoli.rpc.transport.api.converter.ClientMessageConverter;
import com.github.zhaoli.rpc.transport.api.support.netty.AbstractNettyClient;
import com.github.zhaoli.rpc.transport.tcp.codec.TcpDecoder;
import com.github.zhaoli.rpc.transport.tcp.codec.TcpEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * @author zhaoli
 * @date 2018/6/10
 * 相当于一个客户端连接，对应一个Channel
 * 每个服务器的每个接口对应一个Endpoint
 */
@Slf4j
public class TcpClient extends AbstractNettyClient {
    
    @Override
    protected ChannelInitializer initPipeline() {
        log.info("EacyRpcClient initPipeline...");
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("IdleStateHandler", new IdleStateHandler(0, TcpConstant.HEART_BEAT_TIME_OUT, 0))
                        // ByteBuf -> Message 
                        .addLast("LengthFieldPrepender", new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT))
                        // Message -> ByteBuf
                        .addLast("EasyEncoder", new TcpEncoder(getGlobalConfig().getSerializer()))
                        // ByteBuf -> Message
                        .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH, FrameConstant.LENGTH_FIELD_OFFSET, FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                        // Message -> Message
                        .addLast("EasyDecoder", new TcpDecoder(getGlobalConfig().getSerializer()))

                        .addLast("EasyClientHandler", new TcpClientHandler(TcpClient.this));
            }
        };
    }

    @Override
    protected ClientMessageConverter initConverter() {
        return ClientMessageConverter.DEFAULT_IMPL;
    }
}
