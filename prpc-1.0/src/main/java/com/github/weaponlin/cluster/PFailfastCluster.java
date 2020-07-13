package com.github.weaponlin.cluster;

import com.github.weaponlin.client.PRequest;
import com.github.weaponlin.loadbalance.LoadBalance;

/**
 * TODO
 */
public class PFailfastCluster extends PAbstractCluster {

    public PFailfastCluster(LoadBalance loadBalance) {
        super(loadBalance);
    }

    @Override
    public Object request(PRequest request) {
        // do nothing with fail-fast-strategy
        return doRequest(request);
    }
}
