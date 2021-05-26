package com.weaponlin.inf.prpc.api.hello;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelloRequest implements Serializable {

    private static final long serialVersionUID = 2427707179124125575L;

    private Integer size;

    private String message;
}
