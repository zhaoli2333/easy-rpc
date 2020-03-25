package com.github.zhaoli.rpc.invocation.api.support;


import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.config.ReferenceConfig;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import com.github.zhaoli.rpc.common.util.InvokeParamUtil;
import com.github.zhaoli.rpc.invocation.api.Invocation;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author zhaoli
 * @date 2018/6/10
 */
@Slf4j
public abstract class AbstractInvocation implements Invocation {
    
    @Override
    public final RPCResponse invoke(InvokeParam invokeParam, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws RPCException {
        RPCResponse response;
        ReferenceConfig referenceConfig = InvokeParamUtil.extractReferenceConfigFromInvokeParam(invokeParam);
        RPCRequest rpcRequest = InvokeParamUtil.extractRequestFromInvokeParam(invokeParam);
        try {
            response = doInvoke(rpcRequest, referenceConfig,requestProcessor);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RPCException(e, ErrorEnum.TRANSPORT_FAILURE, "transport异常");
        }
        return response;
    }

    /**
     * 执行对应子类的调用逻辑，可以抛出任何异常
     *
     * @return
     * @throws Throwable
     */
    protected abstract RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig,Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable;
}
