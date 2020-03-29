package com.github.zhaoli.rpc.common.domain;

import com.github.zhaoli.rpc.common.util.TypeUtil;
import io.netty.util.Recycler;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by zhaoli on 2017/7/30.
 * <p>
 * 几种使用场景：
 * 1）consumer发起请求时创建，将数据序列化传输到服务器后被回收(encoder)/injvm调用服务完毕后被回收
 * 2）provider收到请求时创建，服务调用完毕后被回收（反序列化直接获得对象，如果是protostuff可以reuse，其他不可reuse，可以recycle）
 * <p>
 */
@Data
public class RPCRequest implements Serializable {
    private String requestId;
    private String interfaceName;
    private String methodName;
    private String[] parameterTypes;
    private Object[] parameters;
    
    public void setParameterTypes(Class[] parameterTypes) {
        String[] paramTypes = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            paramTypes[i] = parameterTypes[i].getName();
        }
        this.parameterTypes = paramTypes;
    }

    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class[] getParameterTypes() {
        Class[] parameterTypeClasses = new Class[parameterTypes.length];
        try {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (TypeUtil.isPrimitive(parameterTypes[i])) {
                    parameterTypeClasses[i] = TypeUtil.map(parameterTypes[i]);
                } else {
                    parameterTypeClasses[i] = Class.forName(parameterTypes[i]);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return parameterTypeClasses;
    }

    public RPCRequest() {
    }

    public String key() {
        return new StringBuilder(interfaceName)
                .append(".")
                .append(methodName)
                .append(".")
                .append(Arrays.toString(parameterTypes))
                .append(".")
                .append(Arrays.toString(parameters)).toString();
    }

    public void recycle() {
        requestId = null;
        interfaceName = null;
        methodName = null;
        parameterTypes = null;
        parameters = null;
    }
}
