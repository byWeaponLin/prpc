package com.github.weaponlin.inf;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PRPCRequest implements Serializable {
    private static final long serialVersionUID = -9031849935007421804L;

    private String serviceName;

    private String methodName;

    private Object[] params;
}
