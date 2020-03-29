package com.github.zhaoli.rpc.transport.tcp.server;


import com.github.zhaoli.rpc.common.domain.*;
import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.enumeration.SerializerType;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.config.GlobalConfig;
import com.github.zhaoli.rpc.config.ServiceConfig;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;

import java.lang.reflect.Method;


/**
 * Created by zhaoli on 2017/7/29.
 * 实际的业务处理器
 * 每个客户端channel都对应一个handler，所以这里的timeoutCount不需要设置成Map
 */
@Slf4j
public class TcpServerHandler extends SimpleChannelInboundHandler<Message> {

    private GlobalConfig globalConfig;

    public TcpServerHandler(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) {

        if(message.getHeader().getType() == MessageType.PING) {
            log.info("收到来自客户端的PING心跳");
            ctx.writeAndFlush(Message.PONG);
            return;
        }

        int serializeType = message.getHeader().getSerializerType();
        Serializer serializer = SerializerType.getSerializerByType(serializeType);
        if(serializer == null) {
            throw new RPCException(ErrorEnum.UNSUPPOTED_SERIALIZE_TYPE, ErrorEnum.UNSUPPOTED_SERIALIZE_TYPE.getErrorCode());
        }

        RPCRequest request = serializer.deserialize(message.getBody(), RPCRequest.class);
        RPCResponse response = new RPCResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable t) {
            log.error(LogLevel.ERROR.name(), t);
            response.setCause(t);
        }

        log.info("已调用完毕服务，结果为: {}", response);

        Header responseHeader = Header.builder()
                .type(MessageType.RESPONSE)
                .version(message.getHeader().getVersion())
                .serializerType(serializeType)
                .build();
        byte[] responseBody = serializer.serialize(response);
        Message responseMessage = Message.builder()
                                        .header(responseHeader)
                                        .body(responseBody)
                                        .build();

        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.writeAndFlush(responseMessage);
        } else {
            log.error("not writable now, message dropped");
        }

//        if(message.getType() == Message.PING) {
//            ctx.writeAndFlush(Message.PONG_MSG);
//            return;
//        }
//        RPCRequest request = message.getRequest();
//        byte serializeType = message.getSerializeType();
//        RPCResponse response = GlobalRecycler.reuse(RPCResponse.class);
//        response.setRequestId(request.getRequestId());
//        try {
//            Object result = handle(request);
//            response.setResult(result);
//        } catch (Throwable t) {
//            log.error(LogLevel.ERROR.name(), t);
//            response.setCause(t);
//        }
//        log.info("已调用完毕服务，结果为: {}", response);
//        ctx.writeAndFlush(Message.buildResponse(serializeType, response));
    }

    /**
     * 反射调用方法
     *
     * @return
     * @throws Throwable
     */
    private Object handle(RPCRequest request) throws Throwable {
        // 查找本地服务
        ServiceConfig serviceConfig = this.globalConfig.getProtocol().referLocalService(request.getInterfaceName());
        Object serviceBean = serviceConfig.getRef();

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);

        return method.invoke(serviceBean, parameters);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("业务异常", cause);
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive()) {
            ctx.close();
        }
    }

//    /**
//     * 当超过规定时间，服务器未读取到来自客户端的请求，则关闭连接
//     *
//     * @param ctx
//     * @param evt
//     * @throws Exception
//     */
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            if (timeoutCount.getAndIncrement() >= TcpConstant.HEART_BEAT_TIME_OUT_MAX_TIME) {
//                ctx.close();
//                log.info("超过丢失心跳的次数阈值，关闭连接");
//            }else {
//                log.info("超过规定时间服务器未收到客户端的心跳或正常信息");
//            }
//        } else {
//            super.userEventTriggered(ctx, evt);
//        }
//    }
}
