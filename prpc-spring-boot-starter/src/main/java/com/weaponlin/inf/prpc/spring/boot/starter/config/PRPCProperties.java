package com.weaponlin.inf.prpc.spring.boot.starter.config;

import com.weaponlin.inf.prpc.config.PRPConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "prpc")
public class PRPCProperties {

    private PRPConfig client;

    private PRPConfig server;
}
