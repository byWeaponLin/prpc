package com.weaponlin.inf.prpc.loader;

import com.weaponlin.inf.prpc.codec.PCodec;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.loadbalance.LoadBalance;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ServiceLoader {

    private static Map<Class<?>, Service<?>> loaders = new ConcurrentHashMap<>();

    private static List<Class<?>> preparedService = Lists.newArrayList(
            LoadBalance.class, PCodec.class
    );


    static {
        /**
         * preheat
         */
        preparedService.forEach(clazz -> {
            try {
                Reflections reflections = new Reflections(clazz);
                final List<Class<?>> candidates = reflections.getSubTypesOf(clazz)
                        .stream()
                        .filter(c -> c.getDeclaredAnnotation(Extension.class) != null)
                        .collect(toList());
                final Service<?> service = new Service<>(candidates);
                loaders.put(clazz, service);
                candidates.forEach(candidate -> {
                    try {
                        Extension extension = candidate.getDeclaredAnnotation(Extension.class);
                        service.loadService(extension.name());
                        log.info("load service candidate: {} success, extension name: {}", candidate.getName(), extension.name());
                    } catch (Exception e) {
                        log.error("load failed, candidate: {}", candidate.getName(), e);
                    }
                });
            } catch (Exception e) {
                log.error("load service failed, service: {}", clazz.getName());
            }
        });
    }

    public static <T> Set<String> getServiceExtension(@NonNull Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new PRPCException("load class must be an interface: " + clazz.getName());
        }

        if (loaders.containsKey(clazz)) {
            return loaders.get(clazz).loadedService.keySet();
        }

        Reflections reflections = new Reflections(clazz);
        final List<Class<? extends T>> candidates = reflections.getSubTypesOf(clazz)
                .stream()
                .filter(c -> c.getDeclaredAnnotation(Extension.class) != null)
                .collect(toList());
        final Service<T> service = new Service<>(candidates);
        service.loadAllService();
        loaders.put(clazz, service);

        return service.loadedService.keySet();
    }

    public synchronized static <T> T getService(@NonNull Class<T> clazz, String extensionName) {
        if (!clazz.isInterface()) {
            throw new PRPCException("load class must be an interface: " + clazz.getName());
        }
        if (StringUtils.isBlank(extensionName)) {
            throw new PRPCException("cant load service for extension is blank");
        }

        if (loaders.containsKey(clazz)) {
            return (T) loaders.get(clazz).loadService(extensionName);
        }

        Reflections reflections = new Reflections(clazz);
        final List<Class<? extends T>> candidates = reflections.getSubTypesOf(clazz)
                .stream()
                .filter(c -> c.getDeclaredAnnotation(Extension.class) != null)
                .collect(toList());
        final Service<T> service = new Service<>(candidates);
        loaders.put(clazz, service);
        return service.loadService(extensionName);
    }


    private static class Service<T> {

        /**
         * candidates
         */
        private List<Class<? extends T>> candidates;
        /**
         * loaded service with name
         */
        private Map<String, T> loadedService;

        Service(List<Class<? extends T>> candidates) {
            this.candidates = candidates;
            this.loadedService = new ConcurrentHashMap<>();
        }

        T loadService(String name) {
            if (loadedService.containsKey(name)) {
                return loadedService.get(name);
            }
            //
            return candidates.stream().filter(clazz -> clazz.getDeclaredAnnotation(Extension.class) != null)
                    .filter(clazz -> clazz.getDeclaredAnnotation(Extension.class).name().equals(name))
                    .findFirst()
                    .map(clazz -> {
                        try {
                            T instance = clazz.getConstructor().newInstance();
                            loadedService.putIfAbsent(name, instance);
                            return instance;
                        } catch (Exception e) {
                            log.error("new instance failed, class: {}", clazz.getName(), e);
                            return null;
                        }
                    }).orElseThrow(() -> new PRPCException("load service failed, not found necessary candidate for " + name));
        }

        void loadAllService() {
            candidates.stream().filter(clazz -> clazz.getDeclaredAnnotation(Extension.class) != null)
                    .forEach(clazz -> {
                        try {
                            Extension extension = clazz.getDeclaredAnnotation(Extension.class);
                            T instance = clazz.getConstructor().newInstance();
                            loadedService.putIfAbsent(extension.name(), instance);
                        } catch (Exception e) {
                            log.error("load instance failed, class: {}", clazz.getName(), e);
                        }
                    });
        }

    }
}
