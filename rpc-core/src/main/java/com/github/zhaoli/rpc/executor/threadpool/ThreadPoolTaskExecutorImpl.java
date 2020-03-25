package com.github.zhaoli.rpc.executor.threadpool;

import com.github.zhaoli.rpc.executor.api.support.AbstractTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhaoli
 * @date 2018/7/19
 */
public class ThreadPoolTaskExecutorImpl extends AbstractTaskExecutor {
    private ExecutorService executorService;
    
    @Override
    public void init(Integer threads) {
        executorService = new ThreadPoolExecutor(
                threads,
                threads,
                0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadFactory() {
                    private AtomicInteger atomicInteger = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r,"threadpool-" + atomicInteger.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );    
    }

    @Override
    public void submit(Runnable runnable) {
        executorService.submit(runnable);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
