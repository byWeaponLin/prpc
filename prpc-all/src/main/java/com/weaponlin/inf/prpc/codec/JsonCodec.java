package com.weaponlin.inf.prpc.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.loader.Extension;
import lombok.Data;

import java.io.IOException;

/**
 * encode/decode with jackson
 */
@Extension(name = "fastjson")
public class JsonCodec implements PCodec {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] encode(Object o) {
        try {
            return objectMapper.writeValueAsBytes(o);
        } catch (Exception e) {
            throw new PRPCException("encode data failed");
        }
    }

    @Override
    public Object decode(byte[] bytes, Object o) {
        try {
            o = objectMapper.readValue(bytes, o.getClass());
            return o;
        } catch (Exception e) {
            throw new PRPCException("decode data failed");
        }
    }
}
