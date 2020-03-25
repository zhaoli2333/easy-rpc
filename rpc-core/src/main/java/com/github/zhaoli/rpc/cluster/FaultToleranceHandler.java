package com.github.zhaoli.rpc.cluster;

import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.common.domain.RPCResponse;

/**
 * @author zhaoli
 * @date 2018/7/22
 * 无状态
 * 注意！
 * 集群容错只对同步调用有效
 */
public interface FaultToleranceHandler {
    RPCResponse handle(ClusterInvoker clusterInvoker, InvokeParam invokeParam,RPCException e);
}
