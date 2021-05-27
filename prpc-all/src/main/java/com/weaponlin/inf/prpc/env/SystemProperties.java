package com.weaponlin.inf.prpc.env;

import org.apache.commons.lang.StringUtils;

import java.util.Optional;

public class SystemProperties {

    public static String getSystemProperty(String key) {
        return getSystemProperty(key, null);
    }

    public static String getSystemProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (StringUtils.isBlank(value)) {
            return Optional.ofNullable(System.getenv(key))
                    .filter(StringUtils::isNotBlank)
                    .orElse(defaultValue);
        }
        return value;
    }

    public static void main(String[] args) {
        System.out.println(getSystemProperty(SystemAttributes.IDC));
    }
}
