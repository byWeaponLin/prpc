package com.weaponlin.inf.prpc.spring.boot.starter.reference;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

public class ReferenceServiceBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        AnnotatedBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(getInterfaceFrom(definition));
        return super.generateBeanName(beanDefinition, registry);
    }

    private Class<?> getInterfaceFrom(BeanDefinition definition) {
        return (Class<?>) definition.getConstructorArgumentValues().getArgumentValue(0, Class.class).getValue();
    }

    protected String buildDefaultBeanName(BeanDefinition definition) {
        String value = super.buildDefaultBeanName(definition);
        return value;
    }
}
