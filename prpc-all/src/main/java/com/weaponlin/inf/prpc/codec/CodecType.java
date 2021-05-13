package com.weaponlin.inf.prpc.codec;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CodecType {

    json,
    protobuf,

    ;

    private static final Map<String, CodecType> MAP = Stream.of(CodecType.values())
            .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static boolean contain(String codec) {
        return MAP.containsKey(codec);
    }
}
