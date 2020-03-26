package com.github.zhaoli.rpc.registry.api.support;

import com.github.zhaoli.rpc.config.RegistryConfig;
import com.github.zhaoli.rpc.registry.api.ServiceRegistry;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry {

    private static final String ZK_REGISTRY_PATH = "/easy";

    protected RegistryConfig registryConfig;

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    protected static String generatePath(String interfaceName) {
        return new StringBuilder(ZK_REGISTRY_PATH).append("/").append(interfaceName).toString();
    }
}
