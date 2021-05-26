package com.weaponlin.inf.prpc.api.hello;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HelloResponse implements Serializable {

    private static final long serialVersionUID = 7958925630628910008L;

    private String greeting;
}
