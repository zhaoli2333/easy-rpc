package com.github.zhaoli.rpc.common.enumeration;

import com.github.zhaoli.rpc.common.enumeration.support.ExtensionBaseType;
import com.github.zhaoli.rpc.proxy.api.RPCProxyFactory;
import com.github.zhaoli.rpc.registry.api.support.AbstractServiceRegistry;
import com.github.zhaoli.rpc.registry.curator.CuratorServiceRegistry;
import com.github.zhaoli.rpc.registry.zookeeper.ZkServiceRegistry;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
public enum RegistryType implements ExtensionBaseType<AbstractServiceRegistry> {

    ZK(new ZkServiceRegistry()),
    CURATOR(new CuratorServiceRegistry());

    private AbstractServiceRegistry abstractServiceRegistry;

    RegistryType(AbstractServiceRegistry abstractServiceRegistry) {
        this.abstractServiceRegistry = abstractServiceRegistry;
    }

    @Override
    public AbstractServiceRegistry getInstance() {
        return abstractServiceRegistry;
    }
}
