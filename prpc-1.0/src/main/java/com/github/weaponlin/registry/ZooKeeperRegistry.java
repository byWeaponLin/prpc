package com.github.weaponlin.registry;

import com.github.weaponlin.config.PRPCConfig;
import com.github.weaponlin.exception.PRpcException;
import com.github.weaponlin.remote.URI;
import com.github.weaponlin.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.github.weaponlin.config.PRPCConfig.*;

/**
 * TODO
 */
@Slf4j
public class ZooKeeperRegistry extends AbstractRegistry {

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
        } catch (IOException e) {
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
                String path = PROVIDER_PATH + "/" + registryProperties.getGroup();
                final Stat exists = zooKeeper.exists(path, false);
                // 先判断服务根路径是否存在
                if (exists == null) {
                    zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                String providerInfo = NetUtils.getLocalHost() + ":" + port;
                zooKeeper.create(path + "/" + service.getName() + ":", providerInfo.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                log.info("register service [{}] success, provider info: {}", service.getName(), providerInfo);
            } catch (KeeperException | InterruptedException e) {
                log.error("register service [{}] failed", service.getName(), e);
            }
        });
    }

    @Override
    public void unregister() {

    }

    @Override
    public void subscribe() {
        // TODO
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
            String basePath = PROVIDER_PATH + "/" + registryProperties.getGroup();
            List<String> children = zooKeeper.getChildren(basePath, true);
            if (CollectionUtils.isEmpty(children)) {
                log.warn("no providers when initializing");
                return;
            }
            children.forEach(child -> {
                try {
                    String[] split = child.split(":");
                    String service = split[0];
                    String provider = new String(zooKeeper.getData(basePath + "/" + child, false, new Stat()));
                    addProvider(service, URI.newURI(provider));
                } catch (Exception e) {
                    log.error("read children data failed", e);
                }
            });
        } catch (KeeperException | InterruptedException e) {
            log.error("zk watch failed", e);
            throw new PRpcException("zk watch failed", e);
        }
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void nodeChanged() {

    }
}