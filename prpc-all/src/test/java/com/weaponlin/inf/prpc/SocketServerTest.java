package com.weaponlin.inf.prpc;

import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

@Deprecated
public class SocketServerTest {
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
                        PRequest request = (PRequest) ois.readObject();
                        System.out.println("request: " + request);

                        // TODO get subType by myself, and supplement cache to improve performance, support interface or pojo class
                        Reflections reflections = new Reflections(request.getServiceName());
                        final Class<?> apiClass = Class.forName(request.getServiceName());
                        final Set<Class<?>> subTypes = reflections.getSubTypesOf((Class<Object>) apiClass);
                        final Class<?> implementationClass = (Class<?>) subTypes.toArray()[0];
                        final Object object = implementationClass.getConstructor().newInstance();

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
