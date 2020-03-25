package com.github.zhaoli.rpc.protocol.api.support;

import com.github.zhaoli.rpc.config.ServiceConfig;
import com.github.zhaoli.rpc.protocol.api.Exporter;
import com.github.zhaoli.rpc.protocol.api.Invoker;

/**
 * @author zhaoli
 * @date 2018/7/15
 */
public abstract class AbstractExporter<T> implements Exporter<T> {
    protected Invoker<T> invoker;
    protected ServiceConfig<T> serviceConfig;
    
    public void setInvoker(Invoker<T> invoker) {
        this.invoker = invoker;
    }

    public void setServiceConfig(ServiceConfig<T> serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }

    @Override
    public ServiceConfig<T> getServiceConfig() {
        return serviceConfig;
    }
}
