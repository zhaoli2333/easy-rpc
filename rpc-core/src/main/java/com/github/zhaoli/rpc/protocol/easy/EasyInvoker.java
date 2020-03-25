package com.github.zhaoli.rpc.protocol.easy;

import com.github.zhaoli.rpc.protocol.api.support.AbstractRemoteInvoker;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author zhaoli
 * @date 2018/7/14
 * 抽象的是一个服务接口的一个服务器地址
 */
@Slf4j
public class EasyInvoker<T> extends AbstractRemoteInvoker<T> {
    @Override
    protected Function<RPCRequest, Future<RPCResponse>> getProcessor() {
        return rpcRequest -> getClient().submit(rpcRequest);
    }
}
