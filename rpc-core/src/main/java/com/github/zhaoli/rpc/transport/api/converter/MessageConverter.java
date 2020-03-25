package com.github.zhaoli.rpc.transport.api.converter;

import com.github.zhaoli.rpc.common.domain.Message;

/**
 * @author zhaoli
 * @date 2018/7/21
 */
public interface MessageConverter {
    Object convert2Object(Message message);
    Message convert2Message(Object obj);
}
