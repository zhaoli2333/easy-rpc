package com.github.zhaoli.rpc.config;

import com.github.zhaoli.rpc.proxy.api.RPCProxyFactory;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationConfig {
    private String name;
    private String serialize;
    private String proxy;
    
    private Serializer serializerInstance;
    private RPCProxyFactory proxyFactoryInstance;
    
}
