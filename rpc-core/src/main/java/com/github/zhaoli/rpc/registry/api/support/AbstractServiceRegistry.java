package com.github.zhaoli.rpc.registry.api.support;

import com.github.zhaoli.rpc.config.RegistryConfig;
import com.github.zhaoli.rpc.registry.api.ServiceRegistry;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry {
    protected RegistryConfig registryConfig;

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }
}
