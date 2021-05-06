package com.github.weaponlin.prpc.registry;

import com.github.weaponlin.prpc.annotation.PRPC;
import com.github.weaponlin.prpc.exception.PRpcException;
import com.github.weaponlin.prpc.config.PRPCConfig;
import com.github.weaponlin.prpc.utils.NetUtils;
import com.github.weaponlin.prpc.remote.URI;
import com.google.common.collect.Lists;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

/**
 * node structure:
 * -$rootPath (根路径)
 * └--prpc
 * |--com.github.weaponlin.api.HelloApi:default  (service_name:group)
 * |       |-providers （服务提供者列表）
 * |       |     |--192.168.1.100:22000
 * |       |     |--192.168.1.110:22000
 * |       |     └--192.168.1.120:11021
 * |       |-consumers （服务调用者列表）
 * |       |     |--192.168.3.100
 * |       |     |--192.168.3.110
 * |       |     └--192.168.3.120
 * |--com.github.weaponlin.api.EchoService:default
 * | ......
 */
@Slf4j
public class ZooKeeperRegistry extends AbstractRegistry {

    public static final String REGISTRY = "zookeeper";

    private static final String PRPC_PATH = "/prpc";

    private static final String SEPARATOR = "/";

    private static final byte[] EMPTY_BYTES = "".getBytes();

    private int port;

    /**
     * TODO
     */
    private List<Class<?>> serviceList;

    private Map<String, List<Class<?>>> groupService = new ConcurrentHashMap<>();

    /**
     * TODO temporary
     */
    @Deprecated
    private PRPCConfig.RegistryProperties registryProperties;

    private ZooKeeper zooKeeper;

    private String basePath;

    public ZooKeeperRegistry(int port, String address, int connectionTimeout) {
        this.port = port;
        this.init(address, connectionTimeout);
    }

    private void init(String address, int connectionTimeout) {
        try {
            this.zooKeeper = new ZooKeeper(address, connectionTimeout, watchEvent -> {
                log.info("watchEvent: {}", watchEvent);
            });
            // TODO self adaption '/' character for zk path
            this.basePath = Stream.of(PRPC_PATH)
                    .filter(StringUtils::isNotBlank)
                    .collect(joining());
            if (zooKeeper.exists(basePath, false) == null) {
                // 初始化basePath
                zooKeeper.create(basePath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            log.error("init zk failed", e);
            throw new PRpcException("init zk failed");
        }
    }

    /**
     * TODO provide server-registry and client-registry
     */

    private void init() {
        try {
            this.zooKeeper = new ZooKeeper(registryProperties.getHost(), registryProperties.getTimeout(), watchEvent -> {
                // TODO watch event
                log.info("watchEvent: {}", watchEvent);
            });
            // TODO self adaption '/' character for zk path
            this.basePath = Stream.of(registryProperties.getPath(), PRPC_PATH)
                    .filter(StringUtils::isNotBlank)
                    .collect(joining());
            if (zooKeeper.exists(basePath, false) == null) {
                // 初始化basePath
                zooKeeper.create(basePath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            log.error("init zk failed", e);
            throw new PRpcException("init zk failed");
        }
    }

    @Override
    public void register() {
        if (CollectionUtils.isEmpty(serviceList)) {
            log.warn("service list is empty");
            return;
        }
        serviceList.stream().filter(Objects::nonNull).forEach(this::register);
    }

    @Override
    public void register(Class<?> service) {
        PRPC prpc = service.getAnnotation(PRPC.class);
        String group = prpc.group();
        if (!groupService.containsKey(group)) {
            groupService.put(group, Lists.newArrayList(service));
        } else {
            groupService.get(group).add(service);
        }
        try {
            String servicePath = basePath + SEPARATOR + service.getName() + ":" + group;
            createZkPathIfNotExist(servicePath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            String providerPath = servicePath + SEPARATOR + "provider";
            createZkPathIfNotExist(providerPath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            String server = NetUtils.getLocalHost() + ":" + port;
            String serverPath = providerPath + SEPARATOR + server;
            createZkPathIfNotExist(serverPath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

            log.info("register service [{}] success, provider info: {}", service.getName(), server);
        } catch (KeeperException | InterruptedException e) {
            log.error("register service [{}] failed", service.getName(), e);
        }
    }

    private void createZkPathIfNotExist(String path, byte[] value, List<ACL> acl, CreateMode createMode)
            throws KeeperException, InterruptedException {
        if (zooKeeper.exists(path, false) == null) {
            zooKeeper.create(path, value, acl, createMode);
        }
    }

    @Override
    public void unregister() {
        groupService.forEach((group, services) -> {
            if (CollectionUtils.isNotEmpty(services)) {
                services.forEach(service -> {
                    String serverPath = basePath + SEPARATOR + service.getName() + ":" + group
                            + SEPARATOR + "provider"
                            + SEPARATOR + NetUtils.getLocalHost() + ":" + port;
                    try {
                        zooKeeper.delete(serverPath, -1);
                        log.info("unregister service [{}] success, port: {}", service.getName(), port);
                    } catch (Exception e) {
                        log.error("unregister service [{}] failed, port: {}", service.getName(), port);
                    }
                });
            }
        });
    }

    @Override
    public void subscribe() {
        refresh();
        try {
            if (MapUtils.isNotEmpty(groupService)) {
                groupService.forEach((group, services) -> {
                    services.stream().filter(Objects::nonNull).forEach(service -> {
                        try {
                            String servicePath = basePath + SEPARATOR + service.getName() + ":" + group;
                            String providerPath = servicePath + SEPARATOR + "provider";

                            zooKeeper.addWatch(providerPath, watchedEvent -> {
                                log.info("path: {}, type: {}, state: {}", watchedEvent.getPath(), watchedEvent.getType(),
                                        watchedEvent.getState());
                                refresh();
                            }, AddWatchMode.PERSISTENT);
                        } catch (Exception e) {
                            log.error("zk watch failed", e);
                        }
                    });
                });
            }
        } catch (Exception e) {
            log.error("zk watch failed", e);
            throw new PRpcException("zk watch failed", e);
        }
    }

    @Override
    public synchronized void subscribe(Class<?> service) {
        PRPC prpc = service.getAnnotation(PRPC.class);
        String group = prpc.group();
        if (!groupService.containsKey(group)) {
            groupService.put(group, Lists.newArrayList(service));
        } else {
            groupService.get(group).add(service);
        }
        try {
            // add watch
            String servicePath = basePath + SEPARATOR + service.getName() + ":" + group;
            String providerPath = servicePath + SEPARATOR + "provider";

            zooKeeper.addWatch(providerPath, watchedEvent -> {
                log.info("path: {}, type: {}, state: {}", watchedEvent.getPath(), watchedEvent.getType(),
                        watchedEvent.getState());
                refresh();
            }, AddWatchMode.PERSISTENT);
            refresh();
        } catch (Exception e) {
            log.error("zk watch failed", e);
        }
    }

    private void discoverService(String service, List<String> serverPath) {
        if (CollectionUtils.isEmpty(serverPath)) {
            return;
        }
        final Set<URI> uris = serverPath.stream().map(URI::newURI)
                .collect(toSet());
        addProvider(service, uris);
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void nodeChanged() {

    }

    @Override
    public void refresh() {
        try {
            // discovery service if config
            if (MapUtils.isNotEmpty(groupService)) {
                groupService.forEach((group, services) -> {
                    if (CollectionUtils.isEmpty(services)) {
                        return;
                    }
                    services.stream().filter(Objects::nonNull).forEach(service -> {
                        try {
                            // discovery services
                            String servicePath = basePath + SEPARATOR + service.getName() + ":" + group;
                            String providerPath = servicePath + SEPARATOR + "provider";

                            List<String> serverPath = zooKeeper.getChildren(providerPath, true);
                            if (CollectionUtils.isNotEmpty(serverPath)) {
                                discoverService(service.getName() + ":" + group, serverPath);
                            }
                            // register consumer
                            String consumerPath = servicePath + SEPARATOR + "consumer";
                            createZkPathIfNotExist(consumerPath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                            String consumer = NetUtils.getLocalHost();
                            String consumerServerPath = consumerPath + SEPARATOR + consumer;
                            createZkPathIfNotExist(consumerServerPath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                        } catch (Exception e) {
                            log.error("discovery service failed, service: {}, group: {}", service.getName(), group);
                        }
                    });
                });
            }
        } catch (Exception e) {
            log.error("zk watch failed", e);
            throw new PRpcException("zk watch failed", e);
        }
    }
}
