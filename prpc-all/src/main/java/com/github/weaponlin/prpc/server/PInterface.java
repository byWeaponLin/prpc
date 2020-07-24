package com.github.weaponlin.prpc.server;

import com.github.weaponlin.prpc.exception.PRpcException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
@Builder
public class PInterface {

    /**
     * TODO
     */
    private String group;

    private String serviceName;

    private Class<?> serviceClass;

    private Object serviceInstance;

    private Map<PMethod, Method> methods;

    private static Map<String, PInterface> cachedInstances = new ConcurrentHashMap<>();

    public static Pair<Object, Method> getInstanceAndMethod(String serviceName, String methodName,
                                                            Class<?>[] parameterTypes) {
        try {
            PInterface pInterface = cachedInstances.get(serviceName);
            if (pInterface == null) {
                Reflections reflections = new Reflections(serviceName);
                final Class<?> apiClass = Class.forName(serviceName);
                final Set<Class<?>> subTypes = reflections.getSubTypesOf((Class<Object>) apiClass);
                final Class<?> implementationClass = (Class<?>) subTypes.toArray()[0];
                Object serviceInstance = implementationClass.getConstructor().newInstance();
                pInterface = PInterface.builder()
                        .serviceName(serviceName)
                        .serviceClass(apiClass)
                        .serviceInstance(serviceInstance)
                        .methods(new ConcurrentHashMap<>())
                        .build();
                cachedInstances.put(serviceName, pInterface);
            }
            return pInterface.getInstanceAndMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new PRpcException("cant extract service instance and method instance", e);
        }
    }

    private Pair<Object, Method> getInstanceAndMethod(String methodName, Class<?>[] parameterTypes) {
        PMethod pmethod = PMethod.newMethod(methodName, parameterTypes);
        if (methods.containsKey(pmethod)) {
            Method methodInstance = methods.get(pmethod);
            return Pair.of(serviceInstance, methodInstance);
        }
        try {
            Method declaredMethod = serviceClass.getDeclaredMethod(methodName, parameterTypes);
            methods.putIfAbsent(pmethod, declaredMethod);
            return Pair.of(serviceInstance, methods.get(pmethod));
        } catch (NoSuchMethodException e) {
            throw new PRpcException(
                    String.format("not found declared method for service: %s, method: %s",
                            serviceName, methodName),
                    e);
        }
    }



    @AllArgsConstructor
    @EqualsAndHashCode
    private static class PMethod {
        private String methodName;

        private Class<?>[] parameterTypes;

        static PMethod newMethod(String methodName, Class<?>[] parameterTypes) {
            return new PMethod(methodName, parameterTypes);
        }
    }
}