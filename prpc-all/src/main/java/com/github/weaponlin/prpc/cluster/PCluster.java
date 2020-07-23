package com.github.weaponlin.prpc.cluster;

import com.github.weaponlin.prpc.client.PRequest;

/**
 * fault strategy
 */
public interface PCluster {

    Object request(PRequest request);
}
