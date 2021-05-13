package com.weaponlin.inf.prpc.registry;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RegistryNaming {
    zookeeper,
    redis,

    ;

    private static final Map<String, RegistryNaming> MAP = Stream.of(RegistryNaming.values())
            .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static boolean contain(String naming) {
        return Optional.ofNullable(naming).filter(StringUtils::isNotBlank)
                .map(MAP::containsKey)
                .orElse(false);
    }
}
