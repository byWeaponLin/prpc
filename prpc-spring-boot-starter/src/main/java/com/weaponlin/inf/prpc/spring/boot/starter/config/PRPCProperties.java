package com.weaponlin.inf.prpc.spring.boot.starter.config;

import com.weaponlin.inf.prpc.config.PRPConfig;
import lombok.Data;

@Data
public class PRPCProperties {

    private PRPConfig client;

    private PRPConfig server;
}
