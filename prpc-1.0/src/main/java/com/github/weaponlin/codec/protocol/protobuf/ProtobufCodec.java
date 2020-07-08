package com.github.weaponlin.codec.protocol.protobuf;

import com.github.weaponlin.codec.protocol.PCodec;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtobufCodec<T> implements PCodec<T> {

    @SuppressWarnings("unchecked")
    @Override
    public byte[] encode(T t) {
        // TODO cache schema with local storage
        Schema schema = RuntimeSchema.getSchema(t.getClass());
        return ProtobufIOUtil.toByteArray(t, schema, LinkedBuffer.allocate(256));
    }

    @Override
    public T decode(byte[] bytes, T t) {
        // TODO
        Schema schema = RuntimeSchema.getSchema(t.getClass());
        ProtobufIOUtil.mergeFrom(bytes, t, schema);
        return t;
    }
}
