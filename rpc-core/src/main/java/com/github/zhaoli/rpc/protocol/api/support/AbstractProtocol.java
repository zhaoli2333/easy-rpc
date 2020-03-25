package com.github.zhaoli.rpc.protocol.api.support;

import com.github.zhaoli.rpc.config.GlobalConfig;
import com.github.zhaoli.rpc.config.ServiceConfig;
import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.protocol.api.Exporter;
import com.github.zhaoli.rpc.protocol.api.Protocol;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author zhaoli
 * @date 2018/7/7
 */
@Slf4j
public abstract class AbstractProtocol implements Protocol {
    private Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();
    private GlobalConfig globalConfig;


    public void init(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    protected GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    protected void putExporter(Class<?> interfaceClass, Exporter<?> exporter) {
        this.exporters.put(interfaceClass.getName(), exporter);
    }

    @Override
    public <T> ServiceConfig<T> referLocalService(String interfaceMame) throws RPCException {
        if (!exporters.containsKey(interfaceMame)) {
            throw new RPCException(ErrorEnum.EXPOSED_SERVICE_NOT_FOUND,"未找到暴露的服务:{}", interfaceMame);
        }
        return (ServiceConfig<T>) exporters.get(interfaceMame).getServiceConfig();
    }

    @Override
    public void close() {

    }
}
