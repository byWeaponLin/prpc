package com.github.weaponlin.server;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PResponse implements Serializable {
    private static final long serialVersionUID = 4271546719542089640L;

    private String requestId;

    private Object exception;

    private Object result;
}