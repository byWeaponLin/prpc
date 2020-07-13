package com.github.weaponlin.cluster;

import com.github.weaponlin.client.PRequest;
import com.github.weaponlin.loadbalance.LoadBalance;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 */
@Slf4j
public class PFailoverCluster extends PAbstractCluster {

    public PFailoverCluster(LoadBalance loadBalance) {
        super(loadBalance);
    }

    @Override
    public Object request(PRequest request) {
        try {
            return doRequest(request);
        } catch (Exception e) {
            log.error("request failed", e);
            // try one time with fail-over-strategy
            return doRequest(request);
        }
    }
}
