package com.github.zhaoli.rpc.invocation.sync;

import com.github.zhaoli.rpc.config.ReferenceConfig;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import com.github.zhaoli.rpc.invocation.api.support.AbstractInvocation;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author zhaoli
 * @date 2018/6/10
 */
@Slf4j
public class SyncInvocation extends AbstractInvocation {
    
    @Override
    protected RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable {
        Future<RPCResponse> future = requestProcessor.apply(rpcRequest);
        RPCResponse response = future.get(referenceConfig.getTimeout(), TimeUnit.MILLISECONDS);
        return response;
    }
}
