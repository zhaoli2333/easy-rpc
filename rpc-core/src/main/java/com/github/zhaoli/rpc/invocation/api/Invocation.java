package com.github.zhaoli.rpc.invocation.api;

import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.common.domain.RPCResponse;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author zhaoli
 * @date 2018/7/7
 */
public interface Invocation {
    RPCResponse invoke(InvokeParam invokeParam, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws RPCException;
}
