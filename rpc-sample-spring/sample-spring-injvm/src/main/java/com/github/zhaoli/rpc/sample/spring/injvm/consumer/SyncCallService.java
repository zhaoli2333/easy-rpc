package com.github.zhaoli.rpc.sample.spring.injvm.consumer;

import com.github.zhaoli.rpc.autoconfig.annotation.RPCReference;
import com.github.zhaoli.rpc.sample.spring.api.domain.User;
import com.github.zhaoli.rpc.sample.spring.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zhaoli
 * @date 2018/6/10
 */
@Slf4j
@Service
public class SyncCallService {
    @RPCReference
    private HelloService helloService;
    
    public void test() throws Exception {
        log.info("sync:{}",helloService.hello(new User("1")));
        log.info("sync:{}",helloService.hello(new User("2")));
        Thread.sleep(3000);
        log.info("sync:{}",helloService.hello(new User("3")));
        Thread.sleep(8000);
        log.info("sync:{}",helloService.hello(new User("4")));
        
    }
}
