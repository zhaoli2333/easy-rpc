package com.github.zhaoli.rpc.cluster.loadbalance;

import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.cluster.support.AbstractLoadBalancer;
import com.github.zhaoli.rpc.common.domain.RPCRequest;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author zhaoli
 * @date 2018/3/11
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {


    @Override
    protected Invoker doSelect(List<Invoker> invokers, RPCRequest request) {
        if (invokers.size() == 0) {
            return null;
        }
        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));

    }
}
