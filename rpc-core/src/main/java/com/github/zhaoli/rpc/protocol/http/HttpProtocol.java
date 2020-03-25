package com.github.zhaoli.rpc.protocol.http;

import com.github.zhaoli.rpc.config.ServiceConfig;
import com.github.zhaoli.rpc.filter.Filter;
import com.github.zhaoli.rpc.registry.api.ServiceURL;
import com.github.zhaoli.rpc.transport.http.client.HttpClient;
import com.github.zhaoli.rpc.transport.http.server.HttpServer;
import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.config.ReferenceConfig;
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
 * @date 2018/7/18
 */
@Slf4j
public class HttpProtocol extends AbstractRemoteProtocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        HttpExporter<T> exporter = new HttpExporter<>();
        exporter.setInvoker(invoker);
        exporter.setServiceConfig(serviceConfig);
        putExporter(invoker.getInterface(), exporter);
        // 启动服务器
        // 这个必须在注册到注册中心之前执行
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
        HttpInvoker<T> invoker = new HttpInvoker<>();
        invoker.setInterfaceClass(referenceConfig.getInterfaceClass());
        invoker.setInterfaceName(referenceConfig.getInterfaceName());
        invoker.setClient(initClient(serviceURL));
        invoker.setGlobalConfig(getGlobalConfig());
        List<Filter> filters = referenceConfig.getFilters();
        if(filters.size() == 0) {
            return invoker;
        }else {
            return invoker.buildFilterChain(filters);
        }
    }

    @Override
    protected Client doInitClient(ServiceURL serviceURL) {
        HttpClient httpClient = new HttpClient();
        httpClient.init(getGlobalConfig(), serviceURL);
        return httpClient;
    }


    @Override
    protected Server doOpenServer() {
        HttpServer httpServer = new HttpServer();
        httpServer.init(getGlobalConfig());
        httpServer.run();
        return httpServer;
    }
}
