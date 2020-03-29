package com.github.zhaoli.rpc.common.enumeration;

import com.github.zhaoli.rpc.common.enumeration.support.ExtensionBaseType;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import com.github.zhaoli.rpc.serialize.hessian.HessianSerializer;
import com.github.zhaoli.rpc.serialize.jdk.JdkSerializer;
import com.github.zhaoli.rpc.serialize.json.JsonSerializer;
import com.github.zhaoli.rpc.serialize.protostuff.ProtostuffSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
public enum SerializerType implements ExtensionBaseType<Serializer> {
    PROTOSTUFF(new ProtostuffSerializer(), 0),
    JDK(new JdkSerializer(), 1),
    HESSIAN(new HessianSerializer(), 2),
    JSON(new JsonSerializer(), 3);

    private static Map<Integer, Serializer> serializerMap = new HashMap<>();

    static {
        for(SerializerType serializerType : SerializerType.values()) {
            serializerMap.put(serializerType.getType(), serializerType.getInstance());
        }
    }

    private Serializer serializer;

    private int type;

    SerializerType(Serializer serializer, int type) {
        this.serializer = serializer;
        this.type = type;
    }

    @Override
    public Serializer getInstance() {
        return serializer;
    }

    public int getType() {
        return type;
    }

    public static Serializer getSerializerByType(int serializeType) {
        return serializerMap.get(serializeType);
    }
}
