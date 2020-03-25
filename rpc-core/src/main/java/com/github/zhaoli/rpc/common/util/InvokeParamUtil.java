package com.github.zhaoli.rpc.common.util;

import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.config.ReferenceConfig;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.protocol.api.support.RPCInvokeParam;

/**
 * @author zhaoli
 * @date 2018/7/19
 */
public class InvokeParamUtil {
    
    public static RPCRequest extractRequestFromInvokeParam(InvokeParam invokeParam) {
        return  ((RPCInvokeParam)invokeParam).getRpcRequest();
    }
    
    public static ReferenceConfig extractReferenceConfigFromInvokeParam(InvokeParam invokeParam) {
        return  ((RPCInvokeParam)invokeParam).getReferenceConfig();
    }
    
}
