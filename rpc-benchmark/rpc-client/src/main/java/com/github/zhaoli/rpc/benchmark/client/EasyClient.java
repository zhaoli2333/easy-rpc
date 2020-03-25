package com.github.zhaoli.rpc.benchmark.client;

import com.github.zhaoli.rpc.benchmark.base.client.AbstractClient;
import com.github.zhaoli.rpc.benchmark.base.service.UserService;
import com.github.zhaoli.rpc.autoconfig.annotation.RPCReference;
import org.springframework.stereotype.Component;

/**
 * @author zhaoli
 * @date 2018/8/23
 */
@Component
public class EasyClient extends AbstractClient {
    @RPCReference
    private UserService userService;
    
    @Override
    protected UserService getUserService() {
        return userService;
    }
}
