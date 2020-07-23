package com.github.weaponlin.prpc.requestor;

import com.github.weaponlin.prpc.client.PRequest;

/**
 * TODO requestor
 */
public interface PRequestor {

    Object request(PRequest request);
}
