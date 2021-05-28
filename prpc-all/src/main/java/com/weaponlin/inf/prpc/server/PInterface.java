package com.weaponlin.inf.prpc.server;

import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.utils.ClassScanUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.List;
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

    // TODO 适配其他协议
    public static Pair<Object, Method> getInstanceAndMethod(String group, String serviceName,
                                                            String methodName, Class<?>[] parameterTypes) {
        try {
            String key = serviceName + ":" + Optional.ofNullable(group)
                    .filter(StringUtils::isNotBlank).orElse("");
            PInterface pInterface = cachedInstances.get(key);
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
                cachedInstances.put(key, pInterface);
            }
            return pInterface.getInstanceAndMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new PRPCException("cant extract service instance and method instance", e);
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
            throw new PRPCException(
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

    public synchronized static void registerInterface(String group, List<Class<?>> services) {
        if (CollectionUtils.isEmpty(services)) {
            return;
        }
        services.forEach(service -> {
            registerInterface(group, service);
        });
    }

    public synchronized static void registerInterface(String group, @NonNull Class<?> service) {
        String key = service.getName() + ":" + Optional.ofNullable(group)
                .filter(StringUtils::isNotBlank).orElse("");
        if (cachedInstances.containsKey(key)) {
            return;
        }

        try {
            // scan implementations
            final Class<?> implementationClass = ClassScanUtil.loadClassByLoader().stream()
                    .filter(clazz -> !clazz.isInterface())
                    .filter(service::isAssignableFrom)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("not found subclass for " + service.getName()));

            // initialize api
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
            throw new PRPCException("register server interface failed", e);
        }
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Setter
    @Getter
    private static class PMethod {
        private String methodName;

        private Class<?>[] parameterTypes;

        static PMethod newMethod(String methodName, Class<?>[] parameterTypes) {
            return new PMethod(methodName, parameterTypes);
        }

        static PMethod newMethod(String methodName) {
            PMethod method = new PMethod();
            method.setMethodName(methodName);
            return method;
        }
    }
}
