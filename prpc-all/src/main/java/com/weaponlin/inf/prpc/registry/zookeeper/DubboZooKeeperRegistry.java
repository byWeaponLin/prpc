package com.weaponlin.inf.prpc.registry.zookeeper;

import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.registry.AbstractRegistry;
import com.weaponlin.inf.prpc.utils.ClassScanUtil;
import com.weaponlin.inf.prpc.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.weaponlin.inf.prpc.config.PRPConfig.PGroup;
import static com.weaponlin.inf.prpc.config.PRPConfig.PRegistryCenter;
import static java.util.stream.Collectors.joining;

/**
 * TODO
 */
@Slf4j
public class DubboZooKeeperRegistry extends AbstractRegistry {

    public static final String REGISTRY = "zookeeper";

    private static final String DUBBO_PATH = "/dubbo";

    private static final String SEPARATOR = "/";

    private static final byte[] EMPTY_BYTES = "".getBytes();

    private int serverPort;

    /**
     * TODO
     */
    private List<PGroup> groups;

    private Map<PGroup, List<Class<?>>> groupMap = new ConcurrentHashMap<>();

    private Map<String, List<Class<?>>> groupService = new ConcurrentHashMap<>();

    private PRegistryCenter registryCenter;

    private ZooKeeper zooKeeper;

    private String basePath;

    public DubboZooKeeperRegistry(PRegistryCenter registryCenter, List<PGroup> groups,
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
            this.basePath = Stream.of(DUBBO_PATH)
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
            groupMap.putIfAbsent(group, serviceList);
            register(group, serviceList);
        });
    }

    private void register(PGroup group, List<Class<?>> serviceList) {
        String groupName = group.getGroup();
        if (!groupService.containsKey(groupName)) {
            groupService.put(groupName, serviceList);
        } else {
            groupService.get(groupName).addAll(serviceList);
        }
        try {
            for (Class<?> service : serviceList) {
                String servicePath = basePath + SEPARATOR + service.getName();
                createZkPathIfNotExist(servicePath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                String providerPath = servicePath + SEPARATOR + "providers";
                createZkPathIfNotExist(providerPath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                String serverPath = dubboProviderPath(group, service);
                createZkPathIfNotExist(serverPath, EMPTY_BYTES, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                log.info("register service [{}] success, group: {}, provider info: {}",
                        service.getName(), groupName, providerPath);
            }

        } catch (KeeperException | InterruptedException e) {
            log.error("register group service failed, group: {}", group, e);
            throw new PRPCException("register group service failed, group: " + group, e);
        }
    }

    private String dubboProviderPath(PGroup group, Class<?> service) {
        StringBuilder sb = new StringBuilder();
        String methods = Stream.of(service.getDeclaredMethods()).map(Method::getName)
                .sorted()
                .collect(joining(","));
        sb.append("dubbo://").append(NetUtils.getLocalHost())
                .append(":")
                .append(serverPort)
                .append("/")
                .append(service.getName())
                .append("?anyhost=true&")
                .append("application=from-prpc&")
                .append("bean.name=ServiceBean:").append(service.getName()).append("&")
                .append("dubbo=2.0.2&")
                .append("generic=false&")
                .append("interface=").append(service.getName()).append("&")
                .append("methods=").append(methods).append("&")
                .append("pid=1111&")
                .append("serialization=").append(group.getCodec()).append("&")
                .append("side=provider&")
                .append("timestamp=").append(System.currentTimeMillis());

        return sb.toString();
    }

    private void createZkPathIfNotExist(String path, byte[] value, List<ACL> acl, CreateMode createMode)
            throws KeeperException, InterruptedException {
        if (zooKeeper.exists(path, false) == null) {
            zooKeeper.create(path, value, acl, createMode);
        }
    }

    @Override
    public void register(Class<?> service) {
        throw new UnsupportedOperationException("not support this method");
    }

    @Override
    public void unregister() {
        groupMap.forEach((group, services) -> {
            if (CollectionUtils.isNotEmpty(services)) {
                services.forEach(service -> {
                    String providerPath = dubboProviderPath(group, service);
                    String serverPath = basePath + SEPARATOR + service.getName()
                            + SEPARATOR + "providers"
                            + SEPARATOR + providerPath;
                    try {
                        zooKeeper.delete(serverPath, -1);
                        log.info("unregister service [{}] success, serverPort: {}", service.getName(), serverPort);
                    } catch (Exception e) {
                        log.error("unregister service [{}] failed, serverPort: {}", service.getName(), serverPort);
                    }
                });
            }
        });
    }

    @Override
    public void subscribe() {
        throw new UnsupportedOperationException("not support for dubbo protocol");
    }

    @Override
    public void subscribe(Class<?> service) {
        throw new UnsupportedOperationException("not support for dubbo protocol");
    }

    @Override
    public void unsubscribe() {
        throw new UnsupportedOperationException("not support for dubbo protocol");
    }

    @Override
    public void nodeChanged() {
        throw new UnsupportedOperationException("not support for dubbo protocol");
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("not support for dubbo protocol");
    }
}
