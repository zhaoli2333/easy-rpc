package com.github.zhaoli.rpc.sample.spring.api.service;


import com.github.zhaoli.rpc.sample.spring.api.domain.User;

/**
 * Created by zhaoli on 2017/7/30.
 */
public interface HelloService {
    String hello(User user);
}
