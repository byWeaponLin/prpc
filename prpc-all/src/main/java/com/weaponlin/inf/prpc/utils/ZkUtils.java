package com.weaponlin.inf.prpc.utils;

import com.weaponlin.inf.prpc.exception.PRPCException;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZkUtils {

    public static final String SEPARATOR = "/";

    public static String analysisZkPath(String path) {
        if (StringUtils.isBlank(path)) {
            throw new PRPCException("invalid zk path: " + path);
        }
        return Stream.of(path.split(SEPARATOR))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(SEPARATOR, SEPARATOR, ""));
    }
}
