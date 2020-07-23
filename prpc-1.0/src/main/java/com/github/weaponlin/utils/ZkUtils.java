package com.github.weaponlin.utils;

import com.github.weaponlin.exception.PRpcException;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZkUtils {

    public static final String SEPARATOR = "/";

    public static String analysisZkPath(String path) {
        if (StringUtils.isBlank(path)) {
            throw new PRpcException("invalid zk path: " + path);
        }
        return Stream.of(path.split(SEPARATOR))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(SEPARATOR, SEPARATOR, ""));
    }
}
