package com.github.zhaoli.rpc.sample.spring.server.service.impl;


import com.github.zhaoli.rpc.autoconfig.annotation.RPCService;
import com.github.zhaoli.rpc.sample.spring.api.domain.User;
import com.github.zhaoli.rpc.sample.spring.api.callback.HelloCallback;
import com.github.zhaoli.rpc.sample.spring.api.service.HelloServiceWithCallback;

/**
 * @author zhaoli
 * @date 2018/6/10
 */
@RPCService(callback = true,callbackMethod = "callback",callbackParamIndex = 1)
public class HelloServiceWithCallbackImpl implements HelloServiceWithCallback {
    @Override
    public void hello(User user, HelloCallback callback) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        throw new RuntimeException("provider side error");
        callback.callback("Hello, " + user.getUsername());
    }
}
