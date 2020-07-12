package com.github.weaponlin.remote;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

@Data
@EqualsAndHashCode
public class URI {

    private String host;

    private int port;

    public static URI newURI(String host, int port) {
        if (StringUtils.isBlank(host)) {
            throw new RuntimeException("host is blank, please check it");
        }
        if (port <= 0) {
            throw new RuntimeException("illegal port");
        }

        URI uri = new URI();
        uri.setHost(host);
        uri.setPort(port);
        return uri;
    }
}
