package com.github.zhaoli.rpc.config;

import com.github.zhaoli.rpc.cluster.FaultToleranceHandler;
import com.github.zhaoli.rpc.cluster.LoadBalancer;
import com.github.zhaoli.rpc.proxy.api.RPCProxyFactory;
import com.github.zhaoli.rpc.registry.api.ServiceRegistry;
import com.github.zhaoli.rpc.executor.api.TaskExecutor;
import com.github.zhaoli.rpc.protocol.api.Protocol;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoli
 * @date 2018/7/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalConfig {
    private ApplicationConfig applicationConfig;
    private ClusterConfig clusterConfig;
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;

    public String getSerializerName() {
        return applicationConfig.getSerialize();
    }

    public Serializer getSerializer() {
        return applicationConfig.getSerializerInstance();
    }
    
    public RPCProxyFactory getProxyFactory() {
        return applicationConfig.getProxyFactoryInstance();
    }
    
    
    public LoadBalancer getLoadBalancer() {
        return clusterConfig.getLoadBalanceInstance();
    }
    
    public FaultToleranceHandler getFaultToleranceHandler() {
        return clusterConfig.getFaultToleranceHandlerInstance();
    }
    
    public ServiceRegistry getServiceRegistry() {
        return registryConfig.getRegistryInstance();
    }
    
    
    public Protocol getProtocol() {
        return protocolConfig.getProtocolInstance();
    }
    
    public TaskExecutor getClientExecutor() {
        return protocolConfig.getExecutor().getClient().getExecutorInstance();
    }
    
    public TaskExecutor getServerExecutor() {
        return protocolConfig.getExecutor().getServer().getExecutorInstance();
    }
    
    public int getPort() {
        return protocolConfig.getPort();
    }
}
