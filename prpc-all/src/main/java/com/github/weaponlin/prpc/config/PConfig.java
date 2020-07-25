package com.github.weaponlin.prpc.config;

import lombok.Data;

import java.util.List;

@Data
public class PConfig {

    private String zookeeper;

    private int connectionTimeout;

    private String cluster;

    private String loadBalance;

    private String codec;

    private List<PService> services;

    @Data
    public static class PService {
        private String group;

        private String zookeeper;

        private int connectionTimeout;
    }
}
