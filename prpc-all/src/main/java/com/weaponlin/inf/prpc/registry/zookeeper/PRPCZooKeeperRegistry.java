package com.weaponlin.inf.prpc.registry.zookeeper;

import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.registry.AbstractRegistry;
import com.weaponlin.inf.prpc.remote.URI;
import com.weaponlin.inf.prpc.utils.ClassScanUtil;
import com.weaponlin.inf.prpc.utils.NetUtils;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
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
public class PRPCZooKeeperRegistry extends AbstractRegistry {

    public static final String REGISTRY = "zookeeper";

    private static final String PRPC_PATH = "/prpc";

    private static final String SEPARATOR = "/";

    private static final byte[] EMPTY_BYTES = "".getBytes();

    private int serverPort;

    /**
     * TODO
     */
    private List<PRPConfig.PGroup> groups;

    private Map<String, List<Class<?>>> groupService = new ConcurrentHashMap<>();

    private PRPConfig.PRegistryCenter registryCenter;

    private ZooKeeper zooKeeper;

    private String basePath;

    public PRPCZooKeeperRegistry(PRPConfig.PRegistryCenter registryCenter, List<PRPConfig.PGroup> groups,
                                 int serverPort, int connectionTimeout) {
        this.serverPort = serverPort;
        this.groups = groups;
        this.registryCenter = registryCenter;
        this.init(registryCenter.getAddress(), connectionTimeout);
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
            throw new PRPCException("init zk failed");
        }
    }

    @Override
    public void register() {
        if (CollectionUtils.isEmpty(groups)) {
            log.warn("group list is empty");
            return;
        }
        groups.stream().forEach(group -> {
            String basePackage = group.getBasePackage();
            List<Class<?>> serviceList = ClassScanUtil.getInterface(basePackage);
            group.setServices(serviceList);
            register(group, serviceList);
        });
    }

    private void register(PRPConfig.PGroup group, List<Class<?>> serviceList) {
        String groupName = group.getGroup();
        if (!groupService.containsKey(groupName)) {
            groupService.put(groupName, serviceList);
        } else {
            groupService.get(groupName).addAll(serviceList);
        }
        try {
            for (Class<?> service : serviceList) {
                String servicePath = basePath + SEPARATOR + service.getName() + ":" + groupName;
                createZkPathIfNotExist(servicePath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                String providerPath = servicePath + SEPARATOR + "provider";
                createZkPathIfNotExist(providerPath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                String serverPath = providerPath + SEPARATOR + getServerUri(group);
                createZkPathIfNotExist(serverPath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                log.info("register service [{}] success, group: {}, provider info: {}", service.getName(), group, serverPath);
            }

        } catch (Exception e) {
            log.error("register group service failed, group: {}", group, e);
            throw new PRPCException("register group service failed, group: " + group, e);
        }
    }

    private String getServerUri(PRPConfig.PGroup group) throws UnsupportedEncodingException {
        String serverUri = "prpc://" + NetUtils.getLocalHost() + ":" + serverPort + "?protocol=prpc&codec="
                + group.getCodec() + "&group=" + group.getGroup();
        return URLEncoder.encode(serverUri, "UTF-8");
    }

    @Override
    @Deprecated
    public void register(Class<?> service) {
        throw new UnsupportedOperationException();
    }

    private void createZkPathIfNotExist(String path, byte[] value, List<ACL> acl, CreateMode createMode)
            throws KeeperException, InterruptedException {
        if (zooKeeper.exists(path, false) == null) {
            zooKeeper.create(path, value, acl, createMode);
        }
    }

    @Override
    public void unregister() {
        groups.forEach(group -> {
            List<Class<?>> services = group.getServices();
            if (CollectionUtils.isNotEmpty(services)) {
                services.forEach(service -> {
                    try {
                        String serverPath = basePath + SEPARATOR + service.getName() + ":" + group.getGroup()
                                + SEPARATOR + "provider"
                                + SEPARATOR + getServerUri(group);
                        zooKeeper.delete(serverPath, -1);
                        log.info("unregister service [{}] success, serverPort: {}", service.getName(), serverPort);
                    } catch (Exception e) {
                        log.error("unregister service [{}] failed, serverPort: {}", service.getName(), serverPort, e);
                    }
                });
            }
        });
    }

    @Override
    public void subscribe() {
        refresh();
        groups.stream().forEach(group -> {
            String basePackage = group.getBasePackage();
            List<Class<?>> serviceList = ClassScanUtil.getInterface(basePackage);
            group.setServices(serviceList);
            groupService.putIfAbsent(group.getGroup(), serviceList);
            serviceList.forEach(service -> {
                try {
                    String servicePath = basePath + SEPARATOR + service.getName() + ":" + group.getGroup();
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
            });
        });
    }

    @Override
    public synchronized void subscribe(Class<?> service) {
        throw new UnsupportedOperationException();
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
    public List<Class<?>> getServices() {
        return groupService.values().stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
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
                            log.error("discovery service failed, service: {}, group: {}", service.getName(), group, e);
                        }
                    });
                });
            }
        } catch (Exception e) {
            log.error("zk watch failed", e);
            throw new PRPCException("zk watch failed", e);
        }
    }
}
