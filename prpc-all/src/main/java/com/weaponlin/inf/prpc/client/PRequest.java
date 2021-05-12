package com.weaponlin.inf.prpc.client;

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
public class PRequest implements Serializable {
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
}
