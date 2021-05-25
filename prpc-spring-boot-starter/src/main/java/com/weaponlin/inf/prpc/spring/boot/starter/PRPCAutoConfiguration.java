package com.weaponlin.inf.prpc.spring.boot.starter;

import com.weaponlin.inf.prpc.spring.boot.starter.config.PRPCProperties;
import com.weaponlin.inf.prpc.spring.boot.starter.reference.ReferenceServiceRegistrar;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(value = PRPCProperties.class)
@Import({ReferenceServiceRegistrar.class})
public class PRPCAutoConfiguration {

}
