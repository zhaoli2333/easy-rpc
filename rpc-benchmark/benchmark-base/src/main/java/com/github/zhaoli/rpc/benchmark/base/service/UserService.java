package com.github.zhaoli.rpc.benchmark.base.service;

import com.github.zhaoli.rpc.benchmark.base.domain.Page;
import com.github.zhaoli.rpc.benchmark.base.domain.User;

/**
 * @author zhaoli
 * @date 2018/8/22
 */
public interface UserService {
    boolean existUser(String email);
    
    boolean createUser(User user);

    User getUser(long id);

    Page<User> listUser(int pageNo);
}
