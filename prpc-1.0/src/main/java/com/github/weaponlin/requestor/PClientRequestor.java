package com.github.weaponlin.requestor;

import com.github.weaponlin.client.PRequest;
import com.github.weaponlin.cluster.PCluster;
import com.github.weaponlin.cluster.PFailfastCluster;
import com.github.weaponlin.config.PRPCConfig;
import com.github.weaponlin.loadbalance.LoadBalance;
import com.github.weaponlin.loadbalance.LoadBalanceFactory;
import com.github.weaponlin.loadbalance.TemporaryLoadBalance;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO client requestor
 */
@Slf4j
public class PClientRequestor implements PRequestor {

    private PCluster pCluster;

    private PRPCConfig config;

    public PClientRequestor(PRPCConfig config) {
        this.config = config;
        LoadBalance loadBalance = LoadBalanceFactory.getLoadBalacer(config.getLoadBalance());
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
