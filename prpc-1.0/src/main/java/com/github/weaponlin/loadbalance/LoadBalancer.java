package com.github.weaponlin.loadbalance;

import com.github.weaponlin.remote.URI;

/**
 * TODO
 */
public interface LoadBalancer {

    URI select();
}
