package com.github.zhaoli.rpc.cluster;

import com.github.zhaoli.rpc.config.ReferenceConfig;
import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.common.domain.RPCRequest;

import java.util.List;

/**
 * @author zhaoli
 * @date 2018/3/11
 */
public interface LoadBalancer {
    Invoker select(List<Invoker> invokers, RPCRequest request);

    <T> Invoker<T> referCluster(ReferenceConfig<T> referenceConfig);
}
