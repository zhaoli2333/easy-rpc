package com.github.zhaoli.rpc.registry.curator;

import com.github.zhaoli.rpc.registry.api.ServiceURLAddOrUpdateCallback;
import com.github.zhaoli.rpc.registry.api.ServiceURLRemovalCallback;
import com.github.zhaoli.rpc.registry.api.support.AbstractServiceRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CuratorServiceRegistry extends AbstractServiceRegistry {
    @Override
    public void init() {

    }

    @Override
    public void discover(String interfaceName, ServiceURLRemovalCallback callback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback) {

    }

    @Override
    public void register(String address, String interfaceName) {

    }

    @Override
    public void close() {

    }

    @Override
    public void unregister(String address, String interfaceName) {

    }
}
