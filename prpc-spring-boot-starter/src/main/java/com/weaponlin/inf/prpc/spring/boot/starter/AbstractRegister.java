package com.weaponlin.inf.prpc.spring.boot.starter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.util.Collection;

public abstract class AbstractRegister implements ResourceLoaderAware, EnvironmentAware, BeanFactoryAware {
    protected static final String STARGATE_CONFIGURATION_BEAN_NAME =
            "cpdinf.stargate-com.baidu.formula.stargate.spring.boot.autoconfigure.StargateProperties";

    protected BeanFactory beanFactory;
    protected ResourceLoader resourceLoader;
    protected Environment environment;

    protected Class<?> getClass(String beanClassName) {
        try {
            return Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new BeanInitializationException("error create bean with class: " + beanClassName, e);
        }
    }

    protected Collection<String> getBasePackages() {
        return AutoConfigurationPackages.get(beanFactory);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
