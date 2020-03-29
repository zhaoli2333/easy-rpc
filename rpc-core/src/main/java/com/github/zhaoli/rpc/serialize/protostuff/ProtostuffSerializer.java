package com.github.zhaoli.rpc.serialize.protostuff;

import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaoli
 * @date 2018/7/12
 */
public class ProtostuffSerializer implements Serializer {
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new RPCException(ErrorEnum.SERIALIZER_ERROR, "序列化失败", e);
        } finally {
            buffer.clear();
        }
    }


    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws RPCException {
        T t;
        try {
            t = cls.newInstance();
        } catch (Exception e) {
            throw new RPCException(ErrorEnum.SERIALIZER_ERROR, "实例化对象失败", e);
        }
        return deserializeOnObject(data, cls, t);
    }

    protected <T> T deserializeOnObject(byte[] data, Class<T> cls, T t) {
        try {
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, t, schema);
            return t;
        } catch (Exception e) {
            throw new RPCException(ErrorEnum.SERIALIZER_ERROR, "反序列化失败", e);
        }
    }

    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }
}
