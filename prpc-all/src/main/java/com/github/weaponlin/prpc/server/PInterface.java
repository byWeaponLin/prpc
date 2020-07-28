package com.github.weaponlin.prpc.server;

import com.github.weaponlin.prpc.annotation.PRPC;
import com.github.weaponlin.prpc.exception.PRpcException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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

    /**
     * TODO preheat instance and methods
     */
    private static Map<String, PInterface> cachedInstances = new ConcurrentHashMap<>();

    public static Pair<Object, Method> getInstanceAndMethod(String group, String serviceName, String methodName,
                                                            Class<?>[] parameterTypes) {
        try {
            PInterface pInterface = cachedInstances.get(serviceName + ":" + group);
            if (pInterface == null) {
                Reflections reflections = new Reflections(serviceName);
                final Class<?> apiClass = Class.forName(serviceName);
                Optional.ofNullable(apiClass.getAnnotation(PRPC.class)).map(PRPC::group)
                        .filter(g -> StringUtils.equals(g, group))
                        .orElseThrow(() -> new PRpcException("no such group service"));
                final Set<Class<?>> subTypes = reflections.getSubTypesOf((Class<Object>) apiClass);
                final Class<?> implementationClass = (Class<?>) subTypes.toArray()[0];
                Object serviceInstance = implementationClass.getConstructor().newInstance();
                pInterface = PInterface.builder()
                        .serviceName(serviceName)
                        .serviceClass(apiClass)
                        .serviceInstance(serviceInstance)
                        .methods(new ConcurrentHashMap<>())
                        .build();
                cachedInstances.put(serviceName + ":" + group, pInterface);
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

    private void registerMethods() {
        Stream.of(serviceClass.getDeclaredMethods()).forEach(method -> {
            PMethod pMethod = PMethod.newMethod(method.getName(), method.getParameterTypes());
            methods.putIfAbsent(pMethod, method);
            log.info("register method success, method: {}, service: {}", method.getName(), serviceName);
        });
    }

    public synchronized static void registerInterface(String group, Class<?> service) {
        String key = service.getName() + ":" + group;
        if (cachedInstances.containsKey(key)) {
            return;
        }
        Reflections reflections = new Reflections(service);

        try {
            // initialize api
            final Set<Class<?>> subTypes = reflections.getSubTypesOf((Class<Object>) service);
            final Class<?> implementationClass = (Class<?>) subTypes.toArray()[0];
            Object serviceInstance = implementationClass.getConstructor().newInstance();
            final PInterface pInterface = PInterface.builder()
                    .serviceName(service.getName())
                    .serviceClass(service)
                    .serviceInstance(serviceInstance)
                    .methods(new ConcurrentHashMap<>())
                    .build();
            cachedInstances.put(key, pInterface);
            log.info("register service instance success, service: {}, group: {}", service.getName(), group);
            // initialize methods
            pInterface.registerMethods();
        } catch (Exception e) {
            throw new PRpcException("register server interface failed");
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
