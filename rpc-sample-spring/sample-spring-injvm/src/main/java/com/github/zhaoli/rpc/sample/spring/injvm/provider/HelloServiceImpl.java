package com.github.zhaoli.rpc.sample.spring.injvm.provider;


import com.github.zhaoli.rpc.sample.spring.api.domain.User;
import com.github.zhaoli.rpc.autoconfig.annotation.RPCService;
import com.github.zhaoli.rpc.sample.spring.api.service.HelloService;

/**
 * Created by zhaoli on 2017/7/30.
 */
@RPCService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(User user) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello, " + user.getUsername();
    }
}
