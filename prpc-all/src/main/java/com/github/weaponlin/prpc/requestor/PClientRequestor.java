package com.github.weaponlin.prpc.requestor;

import com.github.weaponlin.prpc.client.PRequest;
import com.github.weaponlin.prpc.cluster.PCluster;
import com.github.weaponlin.prpc.cluster.PFailfastCluster;
import com.github.weaponlin.prpc.config.PConfig;
import com.github.weaponlin.prpc.loadbalance.LoadBalance;
import com.github.weaponlin.prpc.loader.ServiceLoader;
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
