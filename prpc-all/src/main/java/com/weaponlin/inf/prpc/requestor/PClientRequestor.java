package com.weaponlin.inf.prpc.requestor;

import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.cluster.PCluster;
import com.weaponlin.inf.prpc.cluster.PFailfastCluster;
import com.weaponlin.inf.prpc.config.PConfig;
import com.weaponlin.inf.prpc.loadbalance.LoadBalance;
import com.weaponlin.inf.prpc.loader.ServiceLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO client requestor
 */
@Slf4j
public class PClientRequestor implements PRequestor {

    private PCluster pCluster;

    private PConfig config;

    public PClientRequestor(PConfig config) {
        this.config = config;
        LoadBalance loadBalance = ServiceLoader.getService(LoadBalance.class, config.getLoadBalance());
        // TODO replace by ServiceLoader
        pCluster = new PFailfastCluster(loadBalance);
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
        return pCluster.request(request);
    }
}
