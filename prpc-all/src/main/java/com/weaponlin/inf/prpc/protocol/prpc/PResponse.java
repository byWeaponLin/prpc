package com.weaponlin.inf.prpc.protocol.prpc;

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
public class PResponse implements Serializable {
    private static final long serialVersionUID = 4271546719542089640L;

    private String requestId;

    private Object exception;

    private Object result;

    private Map<String, Object> attachments;
}
