package com.github.zhaoli.rpc.protocol.http;

import com.github.zhaoli.rpc.protocol.api.support.AbstractRemoteInvoker;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author zhaoli
 * @date 2018/7/18
 */
@Slf4j
public class HttpInvoker<T> extends AbstractRemoteInvoker<T> {
    @Override
    protected Function<RPCRequest, Future<RPCResponse>> getProcessor() {
        return rpcRequest -> getClient().submit(rpcRequest);
    }
}
