package com.github.zhaoli.rpc.config.generic;

import com.github.zhaoli.rpc.common.ExtensionLoader;
import com.github.zhaoli.rpc.config.*;
import com.github.zhaoli.rpc.filter.Filter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhaoli
 * @date 2018/7/23
 */
@Slf4j
public class RPCGenericServiceBean {
    private ReferenceConfig referenceConfig;

    public void init(String interfaceName, boolean isAsync, boolean isCallback, boolean isOneway, int timeout, String callbackMethod, int callbackParamIndex,
                     ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        referenceConfig = ReferenceConfig.createReferenceConfig(
                interfaceName,
                null,
                isAsync,
                isCallback,
                isOneway,
                timeout,
                callbackMethod,
                callbackParamIndex,
                true,
                ExtensionLoader.getInstance().load(Filter.class)
        );
        referenceConfig.init(
                GlobalConfig.builder()
                .applicationConfig(applicationConfig)
                .protocolConfig(protocolConfig)
                .registryConfig(registryConfig)
                .clusterConfig(clusterConfig)
                .build()
        );
    }
    
    public Object invoke(String methodName, Class<?>[] parameterTypes, Object[] parameters){
        return  referenceConfig.invokeForGeneric(methodName,parameterTypes,parameters);  
    }
}
