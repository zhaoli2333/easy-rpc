package com.github.zhaoli.rpc.cluster.faulttolerance;

import com.github.zhaoli.rpc.cluster.FaultToleranceHandler;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.cluster.ClusterInvoker;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhaoli
 * @date 2018/7/22
 */
@Slf4j
public class FailFastFaultToleranceHandler implements FaultToleranceHandler {

    @Override
    public RPCResponse handle(ClusterInvoker clusterInvoker, InvokeParam invokeParam, RPCException e) {
        log.error("出错,FailFast! requestId:{}", invokeParam.getRequestId());
        throw e;
    }
}
