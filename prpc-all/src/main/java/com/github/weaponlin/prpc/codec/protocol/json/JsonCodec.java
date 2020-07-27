package com.github.weaponlin.prpc.codec.protocol.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.weaponlin.prpc.codec.protocol.PCodec;
import com.github.weaponlin.prpc.exception.PRpcException;
import com.github.weaponlin.prpc.loader.Extension;

/**
 * encode/decode with jackson
 */
@Extension(name = "json")
public class JsonCodec implements PCodec {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] encode(Object o) {
        try {
            return objectMapper.writeValueAsBytes(o);
        } catch (Exception e) {
            throw new PRpcException("encode data failed");
        }
    }

    @Override
    public Object decode(byte[] bytes, Object o) {
        try {
            return objectMapper.readValue(bytes, o.getClass());
        } catch (Exception e) {
            throw new PRpcException("decode data failed");
        }
    }
}
