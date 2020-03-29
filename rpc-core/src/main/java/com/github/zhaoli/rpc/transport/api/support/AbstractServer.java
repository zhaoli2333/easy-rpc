package com.github.zhaoli.rpc.transport.api.support;

import com.github.zhaoli.rpc.config.GlobalConfig;
import com.github.zhaoli.rpc.transport.api.Server;

/**
 * @author zhaoli
 * @date 2018/7/19
 */
public abstract class AbstractServer implements Server {
    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    protected GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

}
