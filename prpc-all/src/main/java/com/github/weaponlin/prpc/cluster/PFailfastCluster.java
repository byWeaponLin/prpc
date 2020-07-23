package com.github.weaponlin.prpc.cluster;

import com.github.weaponlin.prpc.client.PRequest;
import com.github.weaponlin.prpc.loadbalance.LoadBalance;

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
