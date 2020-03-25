package com.github.zhaoli.rpc.autoconfig;

import com.github.zhaoli.rpc.config.ApplicationConfig;
import com.github.zhaoli.rpc.config.ClusterConfig;
import com.github.zhaoli.rpc.config.ProtocolConfig;
import com.github.zhaoli.rpc.config.RegistryConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhaoli
 * @date 2018/3/10
 */
@ConfigurationProperties(prefix = "rpc")
@Data
public class RPCProperties {
    private ProtocolConfig protocol;
    private ApplicationConfig application;
    private RegistryConfig registry;
    private ClusterConfig cluster;
}
