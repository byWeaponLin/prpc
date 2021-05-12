package com.weaponlin.inf.prpc.cluster;

import com.weaponlin.inf.prpc.protocol.prpc.PRequest;

/**
 * fault strategy
 */
public interface PCluster {

    Object request(PRequest request);
}
