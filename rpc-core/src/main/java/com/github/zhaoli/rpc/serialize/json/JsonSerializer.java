package com.github.zhaoli.rpc.serialize.json;

import com.alibaba.fastjson.JSONObject;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.serialize.api.Serializer;

/**
 * @author zhaoli
 * @date 2018/8/23
 */
public class JsonSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws RPCException {
        return JSONObject.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws RPCException {
        return JSONObject.parseObject(data, cls);
    }
}
