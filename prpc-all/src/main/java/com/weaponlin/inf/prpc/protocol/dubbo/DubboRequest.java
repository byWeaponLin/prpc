package com.weaponlin.inf.prpc.protocol.dubbo;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;
import com.weaponlin.inf.prpc.constants.Constants;
import com.weaponlin.inf.prpc.protocol.prpc.PMeta;
import com.weaponlin.inf.prpc.protocol.prpc.PPacket;
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
public class DubboRequest implements PPacket {

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


    @Override
    public PMeta getMeta() {
        return new PMeta().setServiceName(path).setMethodName(methodName);
    }
}
