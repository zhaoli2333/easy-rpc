package com.github.zhaoli.rpc.proxy;

import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.proxy.api.support.AbstractRPCProxyFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibRPCProxyFactory extends AbstractRPCProxyFactory {
    @Override
    protected <T> T doCreateProxy(Class<T> interfaceClass, Invoker<T> invoker) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                try {
                    return CglibRPCProxyFactory.this.invokeProxyMethod(invoker, method, args);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return null;
            }
        });
        return (T)enhancer.create();
    }
}
