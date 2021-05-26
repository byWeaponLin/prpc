package com.weaponlin.inf.prpc.spring.boot.starter;

import com.weaponlin.inf.prpc.client.PRPClient;
import com.weaponlin.inf.prpc.server.PRPCServer;
import com.weaponlin.inf.prpc.spring.boot.starter.config.PRPCProperties;
import com.weaponlin.inf.prpc.spring.boot.starter.export.ServiceExportingRegister;
import com.weaponlin.inf.prpc.spring.boot.starter.reference.ReferenceServiceRegistrar;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(value = PRPCProperties.class)
@Import({ReferenceServiceRegistrar.class, ServiceExportingRegister.class})
public class PRPCAutoConfiguration {

    @Bean
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
            PRPCServer prpcServer = new PRPCServer(prpcProperties.getServer());
            prpcServer.start();
            return prpcServer;
        }
        return null;
    }
}
