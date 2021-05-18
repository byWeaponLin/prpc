package com.weaponlin.inf.prpc.codec;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.loader.Extension;
import com.weaponlin.inf.prpc.protocol.dubbo.DubboConstants;
import com.weaponlin.inf.prpc.protocol.dubbo.DubboRequest;
import com.weaponlin.inf.prpc.protocol.dubbo.DubboResponse;
import com.weaponlin.inf.prpc.server.PInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Extension(name = "hessian2")
public class Hessian2Codec implements PCodec {

    public static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory();

    @Override
    public byte[] encode(Object o) {
        try {
            if (o instanceof DubboResponse) {
                DubboResponse response = (DubboResponse) o;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(DubboConstants.DEFAULT_OUTPUT_BUFFER_SIZE);
                Hessian2Output hessian2Output = new Hessian2Output(outputStream);
                hessian2Output.setSerializerFactory(SERIALIZER_FACTORY);
                hessian2Output.writeInt(response.getResponseType());
                hessian2Output.writeObject(response.getResult());
                if (MapUtils.isNotEmpty(response.getAttachments())) {
                    hessian2Output.writeObject(response.getAttachments());
                }
                hessian2Output.flush();
                return outputStream.toByteArray();
            } else {
                throw new PRPCException("not support hessian2 except dubbo");
            }
        } catch (Exception e) {
            throw new PRPCException("encode data with hessian2 failed", e);
        }
    }

    @Override
    public Object decode(byte[] bytes, Object o) {
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(bytes);
            Hessian2Input hessian2Input = new Hessian2Input(inputStream);
            hessian2Input.setSerializerFactory(SERIALIZER_FACTORY);

            if (o instanceof DubboRequest) {

                DubboRequest requestBody = new DubboRequest();
                requestBody.setDubboProtocolVersion(hessian2Input.readString());
                requestBody.setPath(hessian2Input.readString());
                requestBody.setVersion(hessian2Input.readString());
                requestBody.setMethodName(hessian2Input.readString());

                String serviceName = requestBody.getPath();
                String methodName = requestBody.getMethodName();
                Pair<Object, Method> instanceAndMethod = PInterface.getInstanceAndMethod(serviceName, methodName);
                Class<?>[] parameterTypes = instanceAndMethod.getRight().getParameterTypes();

                Object[] args;
                Class<?>[] pts;
                String desc = hessian2Input.readString();
                if (desc.length() == 0) {
                    pts = new Class<?>[0];
                    args = new Object[0];
                } else {
                    pts = new Class[parameterTypes.length];
                    args = new Object[parameterTypes.length];
                    for (int i = 0; i < args.length; i++) {
                        try {
                            pts[i] = parameterTypes[i];
                            args[i] = hessian2Input.readObject(pts[i]);
                        } catch (Exception e) {
                            if (log.isWarnEnabled()) {
                                log.warn("Decode argument failed: " + e.getMessage(), e);
                            }
                        }
                    }
                }
                requestBody.setParameterTypes(pts);
                requestBody.setArguments(args);

                Map<String, String> map = (Map<String, String>) hessian2Input.readObject(Map.class);
                if (map != null && map.size() > 0) {
                    requestBody.getAttachments().putAll(map);
                }
                return requestBody;
            } else {
                throw new PRPCException("not support hessian2 except dubbo");
            }
        } catch (Exception e) {
            throw new PRPCException("decode data with hessian2 failed ", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
