package com.whc.serialize;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

/**
 * @author whc
 * @date 2020/10/13
 * @description
 */
public class ProtoStuffSerializer implements Serializer {
    private static Objenesis objenesis = new ObjenesisStd(true);

    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("不能序列化空对象!");
        }
        Schema schema = RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        byte[] protostuff = null;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException("序列化(" + obj.getClass().getName() + ")对象时发生异常!", e);
        } finally {
            buffer.clear();
        }
        return protostuff;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
        }
        T instance = objenesis.newInstance(clazz);
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        ProtostuffIOUtil.mergeFrom(bytes, instance, schema);
        return instance;
    }
}
