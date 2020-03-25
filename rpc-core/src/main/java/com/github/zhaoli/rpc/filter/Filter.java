package com.github.zhaoli.rpc.filter;

import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.protocol.api.InvokeParam;
import com.github.zhaoli.rpc.protocol.api.Invoker;
import com.github.zhaoli.rpc.common.domain.RPCResponse;

/**
 * @author zhaoli
 * @date 2018/7/7
 */
public interface Filter {
    RPCResponse invoke(Invoker invoker, InvokeParam invokeParam) throws RPCException;
}
