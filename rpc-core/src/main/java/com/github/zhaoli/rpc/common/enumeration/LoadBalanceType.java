package com.github.zhaoli.rpc.common.enumeration;

import com.github.zhaoli.rpc.cluster.loadbalance.*;
import com.github.zhaoli.rpc.common.enumeration.support.ExtensionBaseType;
import com.github.zhaoli.rpc.cluster.support.AbstractLoadBalancer;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
public enum LoadBalanceType implements ExtensionBaseType<AbstractLoadBalancer> {
    LEAST_ACTIVE(new LeastActiveLoadBalancer()),
    RANDOM(new RandomLoadBalancer()),
    CONSISTENT_HASH(new ConsistentHashLoadBalancer()),
    ROUND_ROBIN(new RoundRobinLoadBalancer()),
    WEIGHTED_RANDOM(new WeightedRandomLoadBalancer());
    
    private AbstractLoadBalancer loadBalancer;
    
    LoadBalanceType(AbstractLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public AbstractLoadBalancer getInstance() {
        return loadBalancer;
    }
}
