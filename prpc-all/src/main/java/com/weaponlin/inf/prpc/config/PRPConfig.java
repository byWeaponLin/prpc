package com.weaponlin.inf.prpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * protocol: prpc
 * rigistry:
 * naming: zookeeper
 * address: zookeeper://127.0.0.1:2181/prpc/online
 * codec: protobuf
 * package: com.weaponlin.inf.prpc.test # scan package, spring用
 * timeout: 30000
 * groups:
 * - group: default
 * registry:
 * naming: zookeeper
 * address: zookeeper://127.0.0.1:2181/prpc/online
 * codec: protobuf
 * protocol: http
 * timeout: 30000
 * - group: test
 * registry:
 * naming: zookeeper
 * address: zookeeper://127.0.0.1:2181/prpc/online
 * codec: json
 * protocol: dubbo
 * timeout: 30000
 */
@Data
public class PRPConfig {
    /**
     * optional: none, zookeeper and othe will support in the future
     */
    private PRegistry registry;

    /**
     * unit: milliseconds
     */
    private int connectionTimeout = 30000;

    /**
     * @client fault tolerant of cluster
     * optional: failfast, failover
     */
    private String cluster = "failfast";

    /**
     * @client load balance
     * optional: random, roundrobin
     */
    private String loadBalance = "random";

    /**
     * data encode&decode
     * optional: protobuf, json and so on
     */
    private String codec = "protobuf";

    /**
     * application protocol
     * optional: prpc, dubbo, http and so on
     */
    private String protocol = "prpc";

    /**
     * service group
     */
    private String group = "default";

    /**
     * service groups configuration
     */
    private List<PGroup> groups;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PRegistry {
        /**
         * optional: none, zookeeper, redis, etcd
         */
        private String naming = "zookeeper";

        private String address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PGroup {

        /**
         * optional: none, zookeeper and othe will support in the future
         */
        private PRegistry registry;

        /**
         * unit: milliseconds
         */
        private int connectionTimeouts;

        /**
         * @client fault tolerant of cluster
         * optional: failfast, failover
         */
        private String cluster;

        /**
         * @client load balance
         * optional: random, roundrobin
         */
        private String loadBalance;

        /**
         * data encode&decode
         * optional: protobuf, json and so on
         */
        private String codec;

        /**
         * application protocol
         * optional: prpc, dubbo, http and so on
         */
        private String protocol;

        private String group;

        /**
         * TODO
         */
        private String basePackage = "";

        private List<Class<?>> services;
    }

}
