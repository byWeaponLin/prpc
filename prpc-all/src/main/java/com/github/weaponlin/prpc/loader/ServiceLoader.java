package com.github.weaponlin.prpc.loader;

import com.github.weaponlin.prpc.exception.PRpcException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ServiceLoader {

    private static Map<Class<?>, Service<?>> loaders = new ConcurrentHashMap<>();

    public synchronized static <T> T getService(@NonNull Class<T> clazz, String extensionName) {
        if (!clazz.isInterface()) {
            throw new PRpcException("load class must be an interface: " + clazz.getName());
        }
        if (StringUtils.isBlank(extensionName)) {
            throw new PRpcException("cant load service for extension is blank");
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
                    }).orElseThrow(() -> new PRpcException("load service failed, not found necessary candidate for " + name));
        }

    }
}