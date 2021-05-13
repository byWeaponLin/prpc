package com.weaponlin.inf.prpc.protocol;

import com.weaponlin.inf.prpc.exception.PRpcException;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum ProtocolType {
    dubbo,
    prpc,
    brpc,
    http

    ;

    public static final Map<String, ProtocolType> PROTOCOL_MAP = Stream.of(ProtocolType.values())
            .collect(toMap(Enum::name, Function.identity()));

    public static ProtocolType getProtocolType(String protocol) {
        return Optional.ofNullable(PROTOCOL_MAP.get(protocol))
                .orElseThrow(() -> new PRpcException("not support protocol: " + protocol));
    }
}
