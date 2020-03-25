package com.github.zhaoli.rpc.common.enumeration;

import com.github.zhaoli.rpc.common.enumeration.support.ExtensionBaseType;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import com.github.zhaoli.rpc.serialize.hessian.HessianSerializer;
import com.github.zhaoli.rpc.serialize.jdk.JdkSerializer;
import com.github.zhaoli.rpc.serialize.json.JsonSerializer;
import com.github.zhaoli.rpc.serialize.protostuff.ProtostuffSerializer;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
public enum SerializerType implements ExtensionBaseType<Serializer> {
    PROTOSTUFF(new ProtostuffSerializer()),
    JDK(new JdkSerializer()),
    HESSIAN(new HessianSerializer()),
    JSON(new JsonSerializer());
    
    private Serializer serializer;

    SerializerType(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Serializer getInstance() {
        return serializer;
    }
}
