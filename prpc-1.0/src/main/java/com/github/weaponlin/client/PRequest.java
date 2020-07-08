package com.github.weaponlin.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PRequest implements Serializable {
    private static final long serialVersionUID = -9031849935007421804L;

    private String requestId;

    private String serviceName;

    private String methodName;

    private Object[] params;
}
