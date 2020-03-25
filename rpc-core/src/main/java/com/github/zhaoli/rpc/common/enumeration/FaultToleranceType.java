package com.github.zhaoli.rpc.common.enumeration;

import com.github.zhaoli.rpc.cluster.FaultToleranceHandler;
import com.github.zhaoli.rpc.common.enumeration.support.ExtensionBaseType;
import com.github.zhaoli.rpc.cluster.faulttolerance.FailFastFaultToleranceHandler;
import com.github.zhaoli.rpc.cluster.faulttolerance.FailOverFaultToleranceHandler;
import com.github.zhaoli.rpc.cluster.faulttolerance.FailSafeFaultToleranceHandler;

/**
 * @author zhaoli
 * @date 2018/7/22
 */
public enum FaultToleranceType implements ExtensionBaseType<FaultToleranceHandler> {
    FAILOVER(new FailOverFaultToleranceHandler()),
    FAILFAST(new FailFastFaultToleranceHandler()),
    FAILSAFE(new FailSafeFaultToleranceHandler());
    
    private FaultToleranceHandler faultToleranceHandler;

    FaultToleranceType(FaultToleranceHandler faultToleranceHandler) {
        this.faultToleranceHandler = faultToleranceHandler;
    }

    @Override
    public FaultToleranceHandler getInstance() {
        return faultToleranceHandler;
    }
}
