package com.weaponlin.inf.prpc.spring.boot.starter.export;

import com.weaponlin.inf.prpc.client.PRPClient;
import com.weaponlin.inf.prpc.server.PRPCServer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ServiceExporterRegisterBean {

    private Class<?> serviceInterface;

    private Object target;

    private PRPClient prpClient;

    private PRPCServer prpcServer;

    private boolean exported = false;

}
