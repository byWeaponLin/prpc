package com.github.weaponlin;

import com.github.weaponlin.inf.PRPCRequest;
import com.github.weaponlin.util.ByteUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(8888);
        System.out.println("启动服务器....");

        while (true) {
            Socket s = ss.accept();
            new Thread(() -> {
                try {
                    System.out.println("客户端:" + s.getInetAddress().getLocalHost() + "已连接到服务器");

                    while (true) {
                        final InputStream is = s.getInputStream();
                        final OutputStream os = s.getOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(os);
                        ObjectInputStream ois = new ObjectInputStream(is);

                        PRPCRequest request = (PRPCRequest) ois.readObject();
                        Class<?> c = Class.forName(request.getServiceName());
                        Constructor<?> cons = c.getConstructor();
                        Object object = cons.newInstance();
                        Object response = MethodUtils.invokeMethod(object, request.getMethodName(), request.getParams());
                        oos.writeObject(response);
                        oos.flush();
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        }


    }
}
