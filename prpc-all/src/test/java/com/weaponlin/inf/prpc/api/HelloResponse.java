package com.weaponlin.inf.prpc.api;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class HelloResponse implements Serializable {

    private static final long serialVersionUID = 7958925630628910008L;

    private String greeting;
}
