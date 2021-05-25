package com.weaponlin.inf.prpc.spring.boot.starter.reference;

import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.registry.RegistryFactory;
import com.weaponlin.inf.prpc.spring.boot.starter.config.PRPCProperties;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReferenceServiceFactoryBean<T> implements FactoryBean<T> {

    private PRPCProperties rpcProperties;

    private Class<T> serviceInterface;

    private T singletonInstance = null;

    public ReferenceServiceFactoryBean() {
    }

    public ReferenceServiceFactoryBean(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public synchronized T getObject() throws Exception {
//        if (singletonInstance == null) {
//
//            configuration = stargateConfigService.getServiceConfiguration(serviceInterface.getName());
//
//            RegistryFactory registryFactory = ExtensionLocator.getInstance(RegistryFactory.class)
//                    .getExtension("zookeeper");
//            String zookeeperConnect = configuration.getZookeeperConnect();
//
//            URI zookeeperUri = URI.valueOf("zookeeper://" + zookeeperConnect);
//            Registry registry = registryFactory.getRegistry(zookeeperUri);
//            Assert.notNull(registry, "registryFacotyr get Registry failed");
//            ProxyFactory proxyFactory = ExtensionLocator.getInstance(ProxyFactory.class).getExtension("jdk");
//            URI serviceUri = getServiceUri();
//
//            RegistryDirectory<T> directory = new RegistryDirectory<>(
//                    serviceInterface, zookeeperUri, serviceUri);
//            directory.setRegistry(registry);
//            registry.subscribe(serviceUri, directory);
//            Cluster cluster = ExtensionLocator.getInstance(Cluster.class).getExtension(FailfastCluster.NAME);
//            Requestor<T> requester = cluster.merge(directory);
//            singletonInstance = logged(proxyFactory.getProxy(requester));
//        }
//
//        return singletonInstance;
        return null;
    }

//    private URI getServiceUri() {
//        Map<String, String> params = new HashMap<>();
//        params.put(Constants.VERSION_KEY, configuration.getVersion()); // 默认
//        params.put(Constants.GROUP_KEY, configuration.getGroup()); // 默认
//        params.put(Constants.INTERFACE_KEY, serviceInterface.getName());
//        // for consumer register
//        String instance = System.getenv().get("INSTANCE");
//        if (instance == null) {
//            instance = UUID.randomUUID().toString();
//        }
//        params.put(Constants.CONSUMER_ID_KEY, instance);
//
//        String instanceId = stargateConfigService.getInstanceId();
//        if (instanceId != null) {
//            params.put(Constants.INSTANCE_ID_KEY, instanceId);
//        }
//
//        String host = stargateConfigService.getIpaddr();
//        return new URI.Builder("star", host, 0).params(params).build();
//        return null;
//    }



    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
