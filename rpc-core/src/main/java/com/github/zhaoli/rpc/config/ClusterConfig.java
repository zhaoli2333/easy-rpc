package com.github.zhaoli.rpc.config;

import com.github.zhaoli.rpc.cluster.FaultToleranceHandler;
import com.github.zhaoli.rpc.cluster.LoadBalancer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterConfig {
    private String loadbalance;
    private String faulttolerance;
    private LoadBalancer loadBalanceInstance;
    private FaultToleranceHandler faultToleranceHandlerInstance;
    
}
