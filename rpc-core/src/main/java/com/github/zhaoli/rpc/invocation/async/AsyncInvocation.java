package com.github.zhaoli.rpc.invocation.async;

import com.github.zhaoli.rpc.config.ReferenceConfig;
import com.github.zhaoli.rpc.common.context.RPCThreadLocalContext;
import com.github.zhaoli.rpc.common.domain.RPCRequest;
import com.github.zhaoli.rpc.invocation.api.support.AbstractInvocation;
import com.github.zhaoli.rpc.common.domain.RPCResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * @author zhaoli
 * @date 2018/6/10
 */
public class AsyncInvocation extends AbstractInvocation {
    
    @Override
    protected RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable {
        Future<RPCResponse> future = requestProcessor.apply(rpcRequest);
        Future<Object> responseForUser = new Future<Object>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return future.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                RPCResponse response = future.get();
                if (response.hasError()) {
                    throw new ExecutionException(response.getCause());
                } else {
                    return response.getResult();
                }
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                RPCResponse response = future.get(timeout, unit);
                if (response.hasError()) {
                    throw new ExecutionException(response.getCause());
                } else {
                    return response.getResult();
                }
            }
        };
        RPCThreadLocalContext.getContext().setFuture(responseForUser);
        return null;
    }
}
