package com.github.zhaoli.rpc.invocation.oneway;

import com.github.zhaoli.rpc.config.ReferenceConfig;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.invocation.api.support.AbstractInvocation;
import com.github.zhaoli.rpc.common.domain.RPCResponse;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author zhaoli
 * @date 2018/6/10
 */
public  class OneWayInvocation extends AbstractInvocation {

    @Override
    protected RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable {
        requestProcessor.apply(rpcRequest);
        return null;
    }
}
