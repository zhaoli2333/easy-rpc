package com.github.zhaoli.rpc.transport.api.support;

import com.github.zhaoli.rpc.common.enumeration.SerializerType;
import com.github.zhaoli.rpc.config.GlobalConfig;
import com.github.zhaoli.rpc.registry.api.ServiceURL;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import com.github.zhaoli.rpc.transport.api.Client;

/**
 * @author zhaoli
 * @date 2018/7/19
 */
public abstract class AbstractClient implements Client {
    private ServiceURL serviceURL;
    private GlobalConfig globalConfig;
    private int serializeType;
    private Serializer serializer;

    public void init(GlobalConfig globalConfig, ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
        this.globalConfig = globalConfig;
        this.serializeType = SerializerType.valueOf(globalConfig.getSerializerName().toUpperCase()).getType();
        this.serializer = SerializerType.getSerializerByType(this.getSerializeType());
        // 初始化的时候建立连接，才能检测到服务器是否可用
        connect();
    }
    
    protected abstract void connect();

    protected int getSerializeType() {
        return serializeType;
    }

    protected Serializer getSerializer() {
        return serializer;
    }

    protected GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public ServiceURL getServiceURL() {
        return serviceURL;
    }

    @Override
    public void updateServiceConfig(ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
    }
}
