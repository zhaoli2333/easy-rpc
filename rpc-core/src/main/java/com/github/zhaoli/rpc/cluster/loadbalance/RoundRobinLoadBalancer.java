package com.github.zhaoli.rpc.cluster.loadbalance;

import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.cluster.support.AbstractLoadBalancer;
import com.github.zhaoli.rpc.common.domain.RPCRequest;

import java.util.List;

/**
 * @author zhaoli
 * @date 2018/3/11
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    private int index = 0;
    
    @Override
    protected Invoker doSelect(List<Invoker> invokers, RPCRequest request) {
         if(invokers.size() == 0) {
            return null;
        }
        Invoker result = invokers.get(index);
        index = (index + 1) % invokers.size();
        return result;
    }
}
