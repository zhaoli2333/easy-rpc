package com.github.zhaoli.rpc.protocol.api;

/**
 * @author zhaoli
 * @date 2018/7/19
 */
public interface InvokeParam {
    String getInterfaceName();
    
    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getParameters();
    
    String getRequestId();
}
