package com.github.zhaoli.rpc.filter.impl;

import com.github.zhaoli.rpc.common.context.RPCStatus;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.filter.Filter;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhaoli
 * @date 2018/7/7
 */
@Slf4j
public class ActiveLimitFilter implements Filter {

    @Override
    public RPCResponse invoke(Invoker invoker, InvokeParam invokerParam) throws RPCException {
        RPCResponse result = null;
        try {
            log.info("starting,incCount...,{}",invokerParam);
            RPCStatus.incCount(invokerParam.getInterfaceName(), invokerParam.getMethodName(), invoker.getServiceURL().getAddress());
            result = invoker.invoke(invokerParam);
        } catch (RPCException e) {
            log.info("catch exception,decCount...,{}",invokerParam);
            RPCStatus.decCount(invokerParam.getInterfaceName(), invokerParam.getMethodName(), invoker.getServiceURL().getAddress());
            throw e;
        }
        log.info("finished,decCount...,{}",invokerParam);
        RPCStatus.decCount(invokerParam.getInterfaceName(), invokerParam.getMethodName(), invoker.getServiceURL().getAddress());
        return result;
    }
}
