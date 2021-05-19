package com.weaponlin.inf.prpc.protocol.dubbo;

import com.alibaba.com.caucho.hessian.io.SerializerFactory;
import com.weaponlin.inf.prpc.constants.Constants;
import com.weaponlin.inf.prpc.protocol.PPacket;
import com.weaponlin.inf.prpc.protocol.prpc.PMeta;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
@Slf4j
@Data
public class DubboRequest implements PPacket {

    public static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory();

    private String dubboProtocolVersion = Constants.DEFAULT_DUBBO_PROTOCOL_VERSION;
    private String path; // service name
    private String version; // version of service
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] arguments;
    private Map<String, String> attachments = new HashMap<String, String>();
    @Setter
    private boolean isHeartbeat = false;


    @Override
    public PMeta getMeta() {
        return new PMeta().setServiceName(path).setMethodName(methodName)
                .setParameterTypes(parameterTypes).setParams(arguments);
    }

    @Override
    public boolean isHeartbeat() {
        return isHeartbeat;
    }
}
