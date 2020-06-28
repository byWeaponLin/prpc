package com.github.weaponlin.client;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PRequest implements Serializable {
    private static final long serialVersionUID = -9031849935007421804L;

    private String serviceName;

    private String methodName;

    private Object[] params;
}
