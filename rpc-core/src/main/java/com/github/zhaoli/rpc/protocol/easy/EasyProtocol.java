package com.github.zhaoli.rpc.protocol.easy;

import com.github.zhaoli.rpc.config.ReferenceConfig;
import com.github.zhaoli.rpc.config.ServiceConfig;
import com.github.zhaoli.rpc.filter.Filter;
import com.github.zhaoli.rpc.registry.api.ServiceURL;
import com.github.zhaoli.rpc.transport.easy.client.EasyClient;
import com.github.zhaoli.rpc.transport.easy.server.EasyServer;
import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.protocol.api.Exporter;
import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.protocol.api.support.AbstractRemoteProtocol;
import com.github.zhaoli.rpc.transport.api.Client;
import com.github.zhaoli.rpc.transport.api.Server;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author zhaoli
 * @date 2018/7/7
 */
@Slf4j
public class EasyProtocol extends AbstractRemoteProtocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        EasyExporter<T> exporter = new EasyExporter<>();
        exporter.setInvoker(invoker);
        exporter.setServiceConfig(serviceConfig);
        putExporter(invoker.getInterface(), exporter);
        openServer();
        // export
        try {
            serviceConfig.getRegistryConfig().getRegistryInstance().register(InetAddress.getLocalHost().getHostAddress() + ":" + getGlobalConfig().getPort(), serviceConfig.getInterfaceName());
        } catch (UnknownHostException e) {
            throw new RPCException(e, ErrorEnum.READ_LOCALHOST_ERROR, "获取本地Host失败");
        }
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig, ServiceURL serviceURL) throws RPCException {
        EasyInvoker<T> invoker = new EasyInvoker<>();
        invoker.setInterfaceClass(referenceConfig.getInterfaceClass());
        invoker.setInterfaceName(referenceConfig.getInterfaceName());
        invoker.setGlobalConfig(getGlobalConfig());
        invoker.setClient(initClient(serviceURL));
        List<Filter> filters = referenceConfig.getFilters();
        if (filters.size() == 0) {
            return invoker;
        } else {
            return invoker.buildFilterChain(filters);
        }
    }

    @Override
    protected Client doInitClient(ServiceURL serviceURL) {
        EasyClient easyClient = new EasyClient();
        easyClient.init(getGlobalConfig(), serviceURL);
        return easyClient;
    }

    @Override
    protected Server doOpenServer() {
        EasyServer easyServer = new EasyServer();
        easyServer.init(getGlobalConfig());
        easyServer.run();
        return easyServer;
    }
}
