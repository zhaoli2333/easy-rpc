package com.github.zhaoli.rpc.protocol.api.support;

import com.github.zhaoli.rpc.registry.api.ServiceURL;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.common.domain.RPCResponse;

/**
 * @author zhaoli
 * @date 2018/7/22
 */
public abstract class InvokerDelegate<T> extends AbstractInvoker<T> {
    private Invoker<T> delegate;

    
    public InvokerDelegate(Invoker<T> delegate) {
        this.delegate = delegate;
    }

    public Invoker<T> getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return delegate.getInterface().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Invoker) {
            Invoker rhs = (Invoker) obj;
            return delegate.getInterface().equals(rhs.getInterface());
        }
        return false;
    }
    
        
    @Override
    public boolean isAvailable() {
        return delegate.isAvailable();
    }

    @Override
    public Class<T> getInterface() {
        return delegate.getInterface();
    }

    @Override
    public String getInterfaceName() {
        return delegate.getInterfaceName();
    }

    @Override
    public ServiceURL getServiceURL() {
        return delegate.getServiceURL();
    }
    
    @Override
    public abstract RPCResponse invoke(InvokeParam invokeParam) throws RPCException;
}
