package com.github.zhaoli.rpc.proxy.api;

import com.github.zhaoli.rpc.protocol.api.Invoker;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
public interface RPCProxyFactory {
    
    <T> T createProxy(Invoker<T> invoker);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type);
}
