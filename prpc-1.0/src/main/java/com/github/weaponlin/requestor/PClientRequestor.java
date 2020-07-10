package com.github.weaponlin.requestor;

import com.github.weaponlin.client.PRequest;
import com.github.weaponlin.cluster.PCluster;
import com.github.weaponlin.cluster.PFailfastCluster;
import com.github.weaponlin.loadbalance.LoadBalancer;
import com.github.weaponlin.loadbalance.TemporaryLoadBalancer;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO client requestor
 */
@Slf4j
public class PClientRequestor implements PRequestor {

    private PCluster pCluster;

    public PClientRequestor() {
        LoadBalancer loadBalancer = new TemporaryLoadBalancer();
        pCluster = new PFailfastCluster(loadBalancer);
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
