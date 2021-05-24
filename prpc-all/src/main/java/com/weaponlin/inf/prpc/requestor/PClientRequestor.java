package com.weaponlin.inf.prpc.requestor;

import com.weaponlin.inf.prpc.cluster.PCluster;
import com.weaponlin.inf.prpc.cluster.PFailfastCluster;
import com.weaponlin.inf.prpc.config.PRPConfig;
import com.weaponlin.inf.prpc.loadbalance.LoadBalance;
import com.weaponlin.inf.prpc.loader.ServiceLoader;
import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO client requestor
 */
@Slf4j
public class PClientRequestor implements PRequestor {

    private PCluster cluster;

    private PRPConfig.PGroup group;

    public PClientRequestor(PRPConfig.PGroup group) {
        this.group = group;
        LoadBalance loadBalance = ServiceLoader.getService(LoadBalance.class, group.getLoadBalance());
        // TODO replace by ServiceLoader
        cluster = new PFailfastCluster(loadBalance);
    }

    /**
     * TODO load balance
     * TODO fail strategy, eg: failover, failfast, failback, failsafe
     *
     * @param request
     * @return
     */
    @Override
    public Object request(PRequest request) {
        return cluster.request(request);
    }
}
