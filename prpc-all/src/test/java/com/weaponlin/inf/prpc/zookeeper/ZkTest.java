package com.weaponlin.inf.prpc.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZkTest {

    private static final String BASE_SERVICES = "/zk-test";
    private static final String SERVICE_NAME = "/products";

    @Test
    public void add_node() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, (watchedEvent) -> {
            });
            Stat exists = zooKeeper.exists(BASE_SERVICES + SERVICE_NAME, false);
            if (exists == null) {
                zooKeeper.create(BASE_SERVICES + SERVICE_NAME, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String server_path = "127.0.0.1" + ":" + "2181";
            //创建的临时的有序节点
            //临时的话断开连接了可以监听到,有序节点创建代表每一个节点否则相同节点名称无法创建
            zooKeeper.create(BASE_SERVICES + SERVICE_NAME + "/child", server_path.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("产品服务注册成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listener() {
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        executor.submit(() -> {
            try {
                ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, (watchedEvent) -> {
                    System.out.println(watchedEvent.getType() + ": " + watchedEvent.getPath());
                });
                updateServiceList(zooKeeper);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateServiceList(ZooKeeper zooKeeper) {
        try {
            List<String> children = zooKeeper.getChildren(BASE_SERVICES + SERVICE_NAME, true);
            List<String> newServerList = new ArrayList<String>();
            for (String subNode : children) {
                byte[] data = zooKeeper.getData(BASE_SERVICES + SERVICE_NAME + "/" + subNode, false, null);
                String host = new String(data, "utf-8");
                System.out.println("host:" + host);
                newServerList.add(host);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 3000, watchedEvent -> { });
        List<String> children = zk.getChildren("/provider/demo", true);
        System.out.println(children);
        children.forEach(child -> {
            try {
                byte[] data = zk.getData("/provider/demo/" + child, false, new Stat());
                System.out.println(new String(data));
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
