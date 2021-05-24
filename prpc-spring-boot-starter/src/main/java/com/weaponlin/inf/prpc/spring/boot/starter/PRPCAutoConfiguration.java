package com.weaponlin.inf.prpc.spring.boot.starter;

import com.weaponlin.inf.prpc.spring.boot.starter.config.PRPCProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = PRPCProperties.class)
public class PRPCAutoConfiguration {

}
