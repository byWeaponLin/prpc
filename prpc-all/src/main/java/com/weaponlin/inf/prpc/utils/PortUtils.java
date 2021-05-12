package com.weaponlin.inf.prpc.utils;

import com.weaponlin.inf.prpc.exception.PRpcException;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class PortUtils {
    private static Random random = new Random();

    private static final int MAX_COMMON_PORT = 1024;

    private static final int MAX_AVAILABLE_PORT = 65535 - MAX_COMMON_PORT;

    private static final String LOCAL_HOST = "127.0.0.1";

    private static final int MAX_TRY_TIMES = 1000;

    public static int getAvailablePort() {
        for (int i = 0; i < MAX_TRY_TIMES; i++) {
            int port =  random.nextInt(MAX_AVAILABLE_PORT) + MAX_COMMON_PORT;
            if (isAvailable(LOCAL_HOST, port)) {
                return port;
            }
        }
        throw new PRpcException("can't find an available port after 1000 times retry");
    }

    public static boolean isAvailable(String host, int port) {
        try {
            InetAddress Address = InetAddress.getByName(host);
            Socket socket = new Socket(Address,port);  //建立一个Socket连接
            return false;
        } catch (Exception e) {

        }
        return true;
    }
}
