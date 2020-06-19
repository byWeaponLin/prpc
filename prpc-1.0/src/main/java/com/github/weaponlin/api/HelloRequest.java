package com.github.weaponlin.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class HelloRequest implements Serializable {

    private static final long serialVersionUID = 2427707179124125575L;

    private Integer size;

    private String message;
}
