package com.github.zhaoli.rpc.common.enumeration;

import com.github.zhaoli.rpc.common.enumeration.support.ExtensionBaseType;
import com.github.zhaoli.rpc.proxy.CglibRPCProxyFactory;
import com.github.zhaoli.rpc.proxy.JavassistRPCProxyFactory;
import com.github.zhaoli.rpc.proxy.JdkRPCProxyFactory;
import com.github.zhaoli.rpc.proxy.api.RPCProxyFactory;

/**
 * @author zhaoli
 * @date 2018/8/23
 */
public enum ProxyFactoryType implements ExtensionBaseType<RPCProxyFactory> {
    JAVASSIST(new JavassistRPCProxyFactory()),
    JDK(new JdkRPCProxyFactory()),
    CGLIB(new CglibRPCProxyFactory());
    private RPCProxyFactory proxyFactory;
    
    ProxyFactoryType(RPCProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }
    
    @Override
    public RPCProxyFactory getInstance() {
        return proxyFactory;
    }
}