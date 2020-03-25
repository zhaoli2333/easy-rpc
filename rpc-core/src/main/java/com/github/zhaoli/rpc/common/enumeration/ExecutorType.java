package com.github.zhaoli.rpc.common.enumeration;

import com.github.zhaoli.rpc.common.enumeration.support.ExtensionBaseType;
import com.github.zhaoli.rpc.executor.disruptor.DisruptorTaskExecutorImpl;
import com.github.zhaoli.rpc.executor.threadpool.ThreadPoolTaskExecutorImpl;
import com.github.zhaoli.rpc.executor.api.TaskExecutor;

/**
 * @author zhaoli
 * @date 2018/7/21
 */
public enum ExecutorType implements ExtensionBaseType<TaskExecutor> {
    THREADPOOL(new ThreadPoolTaskExecutorImpl()),DISRUPTOR(new DisruptorTaskExecutorImpl());
    private TaskExecutor executor;

    ExecutorType(TaskExecutor executor) {
        this.executor = executor;
    }
    
    @Override
    public TaskExecutor getInstance() {
        return executor;
    }
}
