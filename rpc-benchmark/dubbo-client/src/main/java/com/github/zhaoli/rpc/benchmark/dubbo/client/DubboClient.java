package com.github.zhaoli.rpc.benchmark.dubbo.client;

import com.github.zhaoli.rpc.benchmark.base.client.AbstractClient;
import com.github.zhaoli.rpc.benchmark.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaoli
 * @date 2018/8/22
 */
@Component
public class DubboClient extends AbstractClient {
    @Autowired 
    private UserService userService;
    
    @Override
    protected UserService getUserService() {
        return userService;
    }
}
