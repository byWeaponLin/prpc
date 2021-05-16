package com.weaponlin.inf.prpc.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.loader.Extension;
import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.protocol.prpc.PResponse;
import lombok.Data;

import java.io.IOException;

/**
 * encode/decode with jackson
 */
@Extension(name = "jackson")
public class JsonCodec implements PCodec {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] encode(Object o) {
        try {
            return objectMapper.writeValueAsBytes(o);
        } catch (Exception e) {
            throw new PRPCException("encode data failed", e);
        }
    }

    @Override
    public Object decode(byte[] bytes, Object o) {
        try {
            o = objectMapper.readValue(bytes, o.getClass());
            if (o instanceof PRequest) {
                PRequest req = (PRequest) o;
                Object[] params = req.getParams();
                Class<?>[] parameterTypes = req.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    if (params[i] != null && params[i].getClass() != parameterTypes[i]) {
                        params[i] = objectMapper.convertValue(params[i], parameterTypes[i]);
                    }
                }
            } else if (o instanceof PResponse) {
                PResponse res = (PResponse) o;
                // TODO 考虑数组等其他数据类型
                Object result = res.getResult();
                if (result != null && result.getClass() != res.getResultType()) {
                    res.setResult(objectMapper.convertValue(result, res.getResultType()));
                }
            }
            return o;
        } catch (Exception e) {
            throw new PRPCException("decode data failed", e);
        }
    }
}
