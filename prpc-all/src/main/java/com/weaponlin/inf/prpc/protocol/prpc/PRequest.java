package com.weaponlin.inf.prpc.protocol.prpc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.weaponlin.inf.prpc.protocol.AbstractPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PRequest extends AbstractPacket implements Serializable {
    private static final long serialVersionUID = -9031849935007421804L;

    private String group;

    private String requestId;

    private String serviceName;

    private String methodName;

    private Object[] params;

    private Class<?>[] parameterTypes;

    /**
     * TODO
     */
    private Map<String, Object> attachments;

    @Override
    @JsonIgnore
    public PMeta getMeta() {
        return new PMeta().setRequestId(requestId)
                .setServiceName(serviceName)
                .setMethodName(methodName)
                .setParameterTypes(parameterTypes)
                .setParams(params);
    }
}
