package com.github.zhaoli.rpc.sample.spring.client.generic.service;

import com.github.zhaoli.rpc.config.generic.RPCGenericServiceBean;
import com.github.zhaoli.rpc.config.ApplicationConfig;
import com.github.zhaoli.rpc.config.ClusterConfig;
import com.github.zhaoli.rpc.config.ProtocolConfig;
import com.github.zhaoli.rpc.config.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaoli
 * @date 2018/7/23
 */
@Configuration
public class GenericServiceConfiguration {
    
    
    @Bean(name="helloService")
    public RPCGenericServiceBean helloService(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        RPCGenericServiceBean bean = new RPCGenericServiceBean();
        bean.init(
                "com.github.zhaoli.rpc.sample.spring.api.service.HelloService",
                false,
                false,
                false,
                3000,
                "",
                1,
                applicationConfig,
                clusterConfig,
                protocolConfig,
                registryConfig
        );
        return bean;
    }
}
