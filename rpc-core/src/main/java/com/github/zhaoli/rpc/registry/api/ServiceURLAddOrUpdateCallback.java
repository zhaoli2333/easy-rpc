package com.github.zhaoli.rpc.registry.api;

/**
 * @author zhaoli
 * @date 2018/7/22
 */
@FunctionalInterface
public interface ServiceURLAddOrUpdateCallback {
    void addOrUpdate(ServiceURL serviceURL);
}
