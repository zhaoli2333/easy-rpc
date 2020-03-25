package com.github.zhaoli.rpc.benchmark.server;

import com.github.zhaoli.rpc.benchmark.base.service.UserService;
import com.github.zhaoli.rpc.benchmark.base.service.impl.UserServiceBaseImpl;
import com.github.zhaoli.rpc.autoconfig.annotation.RPCService;

/**
 * @author zhaoli
 * @date 2018/8/23
 */
@RPCService(interfaceClass = UserService.class)
public class UserServiceImpl extends UserServiceBaseImpl {
}
