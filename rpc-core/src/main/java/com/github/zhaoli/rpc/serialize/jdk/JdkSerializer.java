package com.github.zhaoli.rpc.serialize.jdk;

import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.serialize.api.support.AbstractSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author zhaoli
 * @date 2018/7/22
 */
public class JdkSerializer extends AbstractSerializer {

    @Override
    public <T> byte[] serialize(T obj) throws RPCException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] bytes = baos.toByteArray();
            baos.close();
            oos.close();
            return bytes;
        } catch (Throwable e) {
            throw new RPCException(e, ErrorEnum.SERIALIZER_ERROR, "序列化异常:{}", obj);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws RPCException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object o = ois.readObject();
            return cls.cast(o);
        } catch (Throwable e) {
            throw new RPCException(e,ErrorEnum.SERIALIZER_ERROR, "反序列化异常:{}", cls);
        }
    }
}
