package com.weaponlin.inf.prpc.cluster;

import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.loadbalance.LoadBalance;

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
