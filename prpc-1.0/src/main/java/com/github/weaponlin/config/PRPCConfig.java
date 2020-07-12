package com.github.weaponlin.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * TODO
 */
@Data
public class PRPCConfig {

    private String port;

    private String ip;

    private String codec;

    private String failStrategy;

    private String loadBalance;

    /**
     * TODO global registry properties
     */

    private RegistryProperties register;

    private List<RegistryProperties> discovery;

    @Data
    @Accessors(chain = true)
    public static class RegistryProperties {

        /**
         * TODO env type?
         */
        private String type;

        /**
         * zk address
         */
        private String host;

        /**
         * zk path
         */
        private String path;

        /**
         * service group
         */
        private String group;

        private int timeout;
    }
}
