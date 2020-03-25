package com.github.zhaoli.rpc.sample.spring.client.generic.filter;

import com.github.zhaoli.rpc.filter.Filter;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhaoli
 * @date 2018/7/16
 */
@Slf4j
@Component
public class BizLogFilter implements Filter {
    @Override
    public RPCResponse invoke(Invoker invoker, InvokeParam invokeParam) throws RPCException {
        log.info("Biz Logger: invokeParam:{} start!",invokeParam);
        RPCResponse response = invoker.invoke(invokeParam);
        log.info("Biz Logger: invokeParam:{} complele!",invokeParam);
        return response;
    }
}
