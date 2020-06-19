package com.github.weaponlin.inf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

public class PRPCProxy implements InvocationHandler {

    private Object object;
    private Socket s;


    public PRPCProxy(Object object) {
        this.object = object;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        System.out.println("come in");
//        return method.invoke(object, args);

        if (method.getName().equals("toString")) {
            return method.invoke(object, args);
        }
        System.out.println("come in");

        try {
            if (s == null) {
                s = new Socket("127.0.0.1", 8888);
            }
            //构建IO
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

            final PRPCRequest request = PRPCRequest.builder()
                    .serviceName(object.getClass().getName())
                    .methodName(method.getName())
                    .params(args)
                    .build();

            oos.writeObject(request);
            oos.flush();

            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("invoke error");
    }


}
