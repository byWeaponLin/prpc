package com.weaponlin.inf.prpc.protocol.prpc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.weaponlin.inf.prpc.protocol.PPacket;
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
public class PResponse implements PPacket, Serializable {
    private static final long serialVersionUID = 4271546719542089640L;

    private String requestId;

    private String serviceName;

    private String methodName;

    private Object exception;

    private Object result;

    private Class<?> resultType;

    private Map<String, Object> attachments;

    @Override
    @JsonIgnore
    public PMeta getMeta() {
        return new PMeta().setRequestId(requestId)
                .setServiceName(serviceName)
                .setMethodName(methodName);
    }

    @Override
    public boolean isHeartbeat() {
        return false;
    }
}
