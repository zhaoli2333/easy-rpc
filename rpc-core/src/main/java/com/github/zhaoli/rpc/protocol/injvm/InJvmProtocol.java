package com.github.zhaoli.rpc.protocol.injvm;

import com.github.zhaoli.rpc.registry.api.ServiceURL;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.config.ReferenceConfig;
import com.github.zhaoli.rpc.config.ServiceConfig;
import com.github.zhaoli.rpc.protocol.api.Exporter;
import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.protocol.api.support.AbstractProtocol;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhaoli
 * @date 2018/7/18
 */
@Slf4j
public class InJvmProtocol extends AbstractProtocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        InJvmExporter<T> exporter = new InJvmExporter<>();
        exporter.setInvoker(invoker);
        exporter.setServiceConfig(serviceConfig);
        putExporter(invoker.getInterface(), exporter);
        // export
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig, ServiceURL serviceURL) throws RPCException {
        InJvmInvoker<T> invoker = new InJvmInvoker<>();
        invoker.setInterfaceClass(referenceConfig.getInterfaceClass());
        invoker.setInterfaceName(referenceConfig.getInterfaceName());
        invoker.setGlobalConfig(getGlobalConfig());
        return invoker.buildFilterChain(referenceConfig.getFilters());
    }
}
