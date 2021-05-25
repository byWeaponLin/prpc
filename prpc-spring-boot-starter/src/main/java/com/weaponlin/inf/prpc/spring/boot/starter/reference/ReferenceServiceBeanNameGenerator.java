package com.weaponlin.inf.prpc.spring.boot.starter.reference;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ReferenceServiceBeanNameGenerator extends AnnotationBeanNameGenerator {

    private Map<String, String> nameDiffer = new ConcurrentHashMap<>();

    private final AtomicInteger seed = new AtomicInteger(0);

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        AnnotatedBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(getInterfaceFrom(definition));
        return super.generateBeanName(beanDefinition, registry);
    }

    private Class<?> getInterfaceFrom(BeanDefinition definition) {
        return (Class<?>) definition.getConstructorArgumentValues().getArgumentValue(0, Class.class).getValue();
    }

    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String className = definition.getBeanClassName();
        String value = super.buildDefaultBeanName(definition);

        if (!nameDiffer.containsKey(value)) {
            nameDiffer.put(value, className);
        } else if (nameDiffer.containsKey(value) && !nameDiffer.get(value).equals(className)) {
            value += suffix();
            nameDiffer.put(value, className);
        }

        return value;
    }

    private String suffix() {
        return "" + seed.getAndIncrement();
    }
}
