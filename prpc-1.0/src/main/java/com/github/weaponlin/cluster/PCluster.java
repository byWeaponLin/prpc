package com.github.weaponlin.cluster;

import com.github.weaponlin.client.PRequest;

/**
 * fault strategy
 */
public interface PCluster {

    Object request(PRequest request);
}
