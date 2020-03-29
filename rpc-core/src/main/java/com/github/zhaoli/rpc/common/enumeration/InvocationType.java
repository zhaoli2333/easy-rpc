package com.github.zhaoli.rpc.common.enumeration;

import com.github.zhaoli.rpc.config.ReferenceConfig;
import com.github.zhaoli.rpc.common.util.InvokeParamUtil;
import com.github.zhaoli.rpc.invocation.api.Invocation;
import com.github.zhaoli.rpc.invocation.async.AsyncInvocation;
import com.github.zhaoli.rpc.invocation.oneway.OneWayInvocation;
import com.github.zhaoli.rpc.invocation.sync.SyncInvocation;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;

/**
 * @author zhaoli
 * @date 2018/7/15
 */
public enum InvocationType {
    ONEWAY(new OneWayInvocation()),SYNC(new SyncInvocation()),ASYNC(new AsyncInvocation());
    private Invocation invocation;

    InvocationType(Invocation invocation) {
        this.invocation = invocation;
    }
    
    public static Invocation get(InvokeParam invokeParam) {
        ReferenceConfig referenceConfig = InvokeParamUtil.extractReferenceConfigFromInvokeParam(invokeParam);
        if (referenceConfig.isAsync()) {
            return ASYNC.invocation;
        } else if (referenceConfig.isOneWay()) {
           return ONEWAY.invocation;
        } else {
            return SYNC.invocation;
        }
    }
}
