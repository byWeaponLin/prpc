package com.weaponlin.inf.prpc.utils;

import com.weaponlin.inf.prpc.exception.PRpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class NetUtils {

    private static String localHost = "";

    public static String getLocalHost() {
        if (StringUtils.isNotBlank(localHost)) {
            return localHost;
        }
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("get local host failed", e);
            throw new PRpcException("get local host failed");
        }
    }
}
