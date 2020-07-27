package com.github.weaponlin.prpc.config;

import lombok.Data;

import java.util.List;

@Data
public class PConfig {

    /**
     * global zookeeper address and path, eg: zookeeper://127.0.0.1:2181/prpc
     */
    private String zookeeper;

    /**
     * unit: milliseconds
     */
    private int connectionTimeout;

    /**
     * fault tolerant of cluster
     */
    private String cluster;

    /**
     * load balance
     */
    private String loadBalance;

    /**
     * data encode&decode
     * optional:
     */
    private String codec;

    /**
     * service groups configuration
     */
    private List<PGroup> groups;

    @Data
    public static class PGroup {
        /**
         * service group
         */
        private String group;

        /**
         * zookeeper address of a specified group, if null or blank then will use the global zookeeper
         */
        private String zookeeper;

        private int connectionTimeout;
    }
}
