package com.weaponlin.inf.prpc.requestor;

import com.weaponlin.inf.prpc.protocol.prpc.PRequest;

/**
 * TODO requestor
 */
public interface PRequestor {

    Object request(PRequest request);
}
