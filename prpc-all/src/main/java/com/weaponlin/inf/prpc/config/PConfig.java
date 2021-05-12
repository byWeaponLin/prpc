package com.weaponlin.inf.prpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PConfig {

    /**
     * optional: none, zookeeper and othe will support in the future
     */
    private String registry;

    /**
     * global zookeeper address and path, eg: zookeeper://127.0.0.1:2181/prpc
     */
    private String address;

    /**
     * unit: milliseconds
     */
    private int connectionTimeout = 30000;

    /**
     * fault tolerant of cluster
     * optional: failfast, failover
     */
    private String cluster = "failfast";

    /**
     * load balance
     * optional: random, roundrobin
     */
    private String loadBalance = "random";

    /**
     * data encode&decode
     * optional: protobuf, json and so on
     */
    private String codec = "protobuf";

    /**
     * service groups configuration
     */
    private List<PGroup> groups;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PGroup {

        /**
         * service group
         */
        private String group;

        /**
         * zookeeper address of a specified group, if null or blank then will use the global zookeeper
         */
        private String address;

        private int connectionTimeout = 30000;
    }
}
