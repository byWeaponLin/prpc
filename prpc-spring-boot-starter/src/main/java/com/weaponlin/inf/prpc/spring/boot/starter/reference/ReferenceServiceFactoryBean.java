package com.weaponlin.inf.prpc.spring.boot.starter.reference;

import com.weaponlin.inf.prpc.client.PRPClient;
import com.weaponlin.inf.prpc.spring.boot.starter.config.PRPCProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

@Slf4j
public class ReferenceServiceFactoryBean<T> implements FactoryBean<T> {

    @Setter
    @Getter
    private PRPCProperties prpcProperties;

    @Setter
    @Getter
    private PRPClient prpClient;

    @Setter
    @Getter
    private Class<T> serviceInterface;

    private T singletonInstance = null;

    public ReferenceServiceFactoryBean() {
    }

    public ReferenceServiceFactoryBean(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public synchronized T getObject() {
        if (singletonInstance == null) {
            return prpClient.getService(serviceInterface);
        }

        return singletonInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
