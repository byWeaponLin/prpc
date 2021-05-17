package com.weaponlin.inf.prpc.protocol.dubbo;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;
import com.weaponlin.inf.prpc.constants.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
@Slf4j
@Data
public class DubboRequest {

    /**
     * It is important to share the SerializerFactory instance for all Hessian2Input and HessianOutput instances!
     */
    public static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory();

    private String dubboProtocolVersion = Constants.DEFAULT_DUBBO_PROTOCOL_VERSION;
    private String path; // service name
    private String version; // version of service
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] arguments;
    private Map<String, String> attachments = new HashMap<String, String>();


    public byte[] encodeRequestBody() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        hessian2Output.setSerializerFactory(SERIALIZER_FACTORY);
        hessian2Output.writeString(dubboProtocolVersion);
        hessian2Output.writeString(path);
        hessian2Output.writeString(version);
        hessian2Output.writeString(methodName);
        hessian2Output.writeString(ReflectUtils.getDesc(parameterTypes));
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                hessian2Output.writeObject(arguments[i]);
            }
        }
        if (attachments != null) {
            hessian2Output.writeObject(attachments);
        }
        hessian2Output.flushBuffer();
        return outputStream.toByteArray();
    }

    public static DubboRequest decodeRequestBody(ByteBuf requestBodyBuf) throws IOException {
        ByteBufInputStream inputStream = null;
        try {
            inputStream = new ByteBufInputStream(requestBodyBuf, true);
            Hessian2Input hessian2Input = new Hessian2Input(inputStream);
            hessian2Input.setSerializerFactory(SERIALIZER_FACTORY);
            DubboRequest requestBody = new DubboRequest();
            requestBody.setDubboProtocolVersion(hessian2Input.readString());
            requestBody.setPath(hessian2Input.readString());
            requestBody.setVersion(hessian2Input.readString());
            requestBody.setMethodName(hessian2Input.readString());

            String serviceName = requestBody.getPath();
            String methodName = requestBody.getMethodName();
            Class<?> serviceClass = Class.forName(serviceName);
            Method method = null;
            for (Method declaredMethod : serviceClass.getDeclaredMethods()) {
                if (declaredMethod.getName().equals(methodName)) {
                    method = declaredMethod;
                    break;
                }
            }
            Class<?>[] parameterTypes = method.getParameterTypes();


            Object[] args;
            Class<?>[] pts;
            String desc = hessian2Input.readString();
            if (desc.length() == 0) {
                pts = new Class<?>[0];
                args = new Object[0];
            } else {
                // 优化反射
//                pts = ReflectUtils.desc2classArray(desc);
                pts = new Class[parameterTypes.length];
                args = new Object[parameterTypes.length];
                for (int i = 0; i < args.length; i++) {
                    try {
                        pts[i] = (Class<?>) parameterTypes[i];
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
        } catch (Exception e) {
            throw new IOException("Read invocation data failed.", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
