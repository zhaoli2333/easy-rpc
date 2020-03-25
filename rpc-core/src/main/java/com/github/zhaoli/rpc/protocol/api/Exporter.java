package com.github.zhaoli.rpc.protocol.api;

import com.github.zhaoli.rpc.config.ServiceConfig;

/**
 * @author zhaoli
 * @date 2018/7/7
 */
public interface Exporter<T> {
     Invoker<T> getInvoker();
     ServiceConfig<T> getServiceConfig();
    /**
     * unexport.
     * <p>
     * <code>
     * getInvoker().destroy();
     * </code>
     */
    void unexport();
}
