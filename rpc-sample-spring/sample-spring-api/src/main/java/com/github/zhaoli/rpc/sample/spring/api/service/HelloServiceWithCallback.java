package com.github.zhaoli.rpc.sample.spring.api.service;

import com.github.zhaoli.rpc.sample.spring.api.callback.HelloCallback;
import com.github.zhaoli.rpc.sample.spring.api.domain.User;

/**
 * @author zhaoli
 * @date 2018/6/10
 */
public interface HelloServiceWithCallback {
     void hello(User user, HelloCallback callback);
}
