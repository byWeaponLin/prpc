package com.weaponlin.inf.prpc.spring.boot.starter;

import com.weaponlin.inf.prpc.client.PRPClient;
import com.weaponlin.inf.prpc.server.PRPCServer;
import com.weaponlin.inf.prpc.spring.boot.starter.config.PRPCProperties;
import com.weaponlin.inf.prpc.spring.boot.starter.reference.ReferenceServiceRegistrar;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(value = PRPCProperties.class)
@Import({ReferenceServiceRegistrar.class})
public class PRPCAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "prpc")
    public PRPCProperties prpcProperties() {
        return new PRPCProperties();
    }

    @Bean
    public PRPClient prpClient(PRPCProperties prpcProperties) {
        if (prpcProperties.getClient() != null) {
            return new PRPClient(prpcProperties.getClient());
        }
        return null;
    }

    @Bean
    public PRPCServer prpcServer(PRPCProperties prpcProperties) {
        if (prpcProperties.getServer() != null) {
            return new PRPCServer(prpcProperties.getServer());
        }
        return null;
    }
}
