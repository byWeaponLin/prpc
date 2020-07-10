package com.github.weaponlin.cluster;

import com.github.weaponlin.client.PRequest;
import com.github.weaponlin.loadbalance.LoadBalancer;

/**
 * TODO
 */
public class PFailfastCluster extends PAbstractCluster {

    public PFailfastCluster(LoadBalancer loadBalancer) {
        super(loadBalancer);
    }

    @Override
    public Object request(PRequest request) {
        // do nothing with fail-fast-strategy
        return doRequest(request);
    }
}
