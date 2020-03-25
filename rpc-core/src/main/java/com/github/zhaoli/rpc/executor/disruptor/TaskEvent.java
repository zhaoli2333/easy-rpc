package com.github.zhaoli.rpc.executor.disruptor;

import lombok.Data;

/**
 * @author zhaoli
 * @date 2018/7/21
 */
@Data
public class TaskEvent {
    private Runnable task;
}
