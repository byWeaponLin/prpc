package com.github.weaponlin.config;

import lombok.Data;

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

    private RegistryProperties register;

    private List<RegistryProperties> discovery;

    @Data
    public static class RegistryProperties {

        private String type;

        private String host;

        private String path;

        private String group;
    }
}
