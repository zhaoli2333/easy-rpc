package com.github.zhaoli.rpc.registry.api;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
public interface ServiceRegistry {
    void init();
    void discover(String interfaceName, ServiceURLRemovalCallback callback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback);
    void register(String address, String interfaceName);
    void close();
    void unregister(String address, String interfaceName);
}
