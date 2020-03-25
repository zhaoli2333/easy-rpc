package com.github.zhaoli.rpc.transport.api;

import com.github.zhaoli.rpc.registry.api.ServiceURL;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Future;

/**
 * @author zhaoli
 * @date 2018/7/19
 */
public interface Client {
    Future<RPCResponse> submit(RPCRequest request);

    void close();

    ServiceURL getServiceURL();

    void handleException(Throwable throwable);
    
    void handleCallbackRequest(RPCRequest request, ChannelHandlerContext ctx);
    
    void handleRPCResponse(RPCResponse response);

    boolean isAvailable();
    
    void updateServiceConfig(ServiceURL serviceURL);

}
