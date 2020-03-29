package com.github.zhaoli.rpc.protocol.injvm;

import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.common.util.InvokeParamUtil;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.protocol.api.support.AbstractInvoker;
import com.github.zhaoli.rpc.common.domain.RPCResponse;

import java.lang.reflect.Method;

/**
 * @author zhaoli
 * @date 2018/7/18
 */
public class InJvmInvoker<T> extends AbstractInvoker<T> {

    @Override
    public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
        Object serviceBean = getGlobalConfig().getProtocol().referLocalService(invokeParam.getInterfaceName()).getRef();
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = invokeParam.getMethodName();
        Class<?>[] parameterTypes = invokeParam.getParameterTypes();
        Object[] parameters = invokeParam.getParameters();
        RPCResponse response = new RPCResponse();
        response.setRequestId(invokeParam.getRequestId());
        try {
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);
            response.setResult(result);
            // 回收request
            InvokeParamUtil.extractRequestFromInvokeParam(invokeParam).recycle();
        } catch (Throwable t) {
            t.printStackTrace();
            response.setCause(t);
        }
        return response;
    }

}
