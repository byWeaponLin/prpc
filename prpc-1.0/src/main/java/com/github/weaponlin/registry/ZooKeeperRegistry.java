package com.github.weaponlin.registry;

import com.github.weaponlin.exception.PRpcException;
import com.github.weaponlin.remote.URI;
import com.github.weaponlin.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.github.weaponlin.config.PRPCConfig.RegistryProperties;
import static java.util.stream.Collectors.joining;

/**
 * node structure:
 * -$rootPath (根路径)
 *        └--prpc
 *            |--com.github.weaponlin.api.HelloApi:default
 *            |       |-providers （服务提供者列表）
 *            |       |     |--192.168.1.100:22000
 *            |       |     |--192.168.1.110:22000
 *            |       |     └--192.168.1.120:11021
 *            |       |-consumers （服务调用者列表）
 *            |       |     |--192.168.3.100
 *            |       |     |--192.168.3.110
 *            |       |     └--192.168.3.120
 *            |--com.github.weaponlin.api.EchoService:default
 *            | ......
 */
@Slf4j
public class ZooKeeperRegistry extends AbstractRegistry {

    private static final String PRPC_PATH = "/prpc";

    private static final String SEPARATOR = "/";

    private static final byte[] EMPTY_BYTES = "".getBytes();

    private int port;

    /**
     * TODO
     */
    private List<Class<?>> serviceList;

    /**
     * TODO temporary
     */
    private RegistryProperties registryProperties;

    private ZooKeeper zooKeeper;

    private String basePath;

    public ZooKeeperRegistry(int port, List<Class<?>> serviceList, RegistryProperties registryProperties) {
        this.port = port;
        this.serviceList = serviceList;
        this.registryProperties = registryProperties;
        this.init();
    }

    public ZooKeeperRegistry(List<Class<?>> serviceList, RegistryProperties registryProperties) {
        this.serviceList = serviceList;
        this.registryProperties = registryProperties;
        this.init();
    }

    /**
     * TODO provide server-registry and client-registry
     */

    private void init() {
        try {
            this.zooKeeper = new ZooKeeper(registryProperties.getHost(), registryProperties.getTimeout(), watchEvent -> {
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
        serviceList.stream().filter(Objects::nonNull).forEach(service -> {
            try {

                String servicePath = basePath + SEPARATOR + service.getName() + ":" + registryProperties.getGroup();
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
        });
    }

    private void createZkPathIfNotExist(String path, byte[] value, List<ACL> acl, CreateMode createMode)
            throws KeeperException, InterruptedException {
        if (zooKeeper.exists(path, false) == null) {
            zooKeeper.create(path, value, acl, createMode);
        }
    }

    @Override
    public void unregister() {

    }

    @Override
    public void subscribe() {
        // TODO remove client init
        clientInit();
        try {
            String basePath = PROVIDER_PATH + "/" + registryProperties.getGroup();
            zooKeeper.addWatch(basePath, watchedEvent -> {
                final Watcher.Event.EventType eventType = watchedEvent.getType();

                log.info("path: {}, type: {}, state: {}", watchedEvent.getPath(), watchedEvent.getType(), watchedEvent.getState());
                if (eventType == Watcher.Event.EventType.NodeCreated) {

                } else if (eventType == Watcher.Event.EventType.NodeDeleted) {

                } else if (eventType == Watcher.Event.EventType.NodeDataChanged) {

                } else if (eventType == Watcher.Event.EventType.NodeChildrenChanged) {

                }
            }, AddWatchMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            log.error("zk watch failed", e);
            throw new PRpcException("zk watch failed", e);
        }
    }

    private void clientInit() {
        try {
            // discovery service if config
            if (CollectionUtils.isNotEmpty(serviceList)) {
                serviceList.stream().filter(Objects::nonNull).forEach(service -> {
                    try {
                        String servicePath = basePath + SEPARATOR + service.getName() + ":" + registryProperties.getGroup();
                        String providerPath = servicePath + SEPARATOR + "provider";

                        List<String> serverPath = zooKeeper.getChildren(providerPath, true);
                        if (CollectionUtils.isNotEmpty(serverPath)) {
                            discoverService(service.getName(), serverPath);
                        }

                    } catch (Exception e) {
                        log.error("discovery service failed, service: {}, group: {}", service.getName(), registryProperties.getGroup());
                    }
                });
            }
            // TODO if not config service then discovery all?
//            String basePath = PROVIDER_PATH + "/" + registryProperties.getGroup();
//            List<String> children = zooKeeper.getChildren(basePath, true);
//            if (CollectionUtils.isEmpty(children)) {
//                log.warn("no providers when initializing");
//                return;
//            }
//            children.forEach(child -> {
//                try {
//                    String[] split = child.split(":");
//                    String service = split[0];
//                    String provider = new String(zooKeeper.getData(basePath + "/" + child, false, new Stat()));
//                    addProvider(service, URI.newURI(provider));
//                } catch (Exception e) {
//                    log.error("read children data failed", e);
//                }
//            });
        } catch (Exception e) {
            log.error("zk watch failed", e);
            throw new PRpcException("zk watch failed", e);
        }
    }

    private void discoverService(String service, List<String> serverPath) {
        if (CollectionUtils.isEmpty(serverPath)) {
            return;
        }
        serverPath.forEach(server -> {
            try {
                addProvider(service, URI.newURI(server));
            } catch (Exception e) {
                log.error("read children data failed", e);
            }
        });
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void nodeChanged() {

    }
}
