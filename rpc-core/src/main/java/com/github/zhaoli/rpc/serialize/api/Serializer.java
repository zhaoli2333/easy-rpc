package com.github.zhaoli.rpc.serialize.api;

import com.github.zhaoli.rpc.common.exception.RPCException;

/**
 * @author zhaoli
 * @date 2018/7/12
 */
public interface Serializer {
    <T> byte[] serialize(T obj) throws RPCException;
    <T> T deserialize(byte[] data, Class<T> cls) throws RPCException;
}
