package com.github.weaponlin.loadbalance;

import com.github.weaponlin.remote.URI;

/**
 * TODO
 */
public interface LoadBalance {

    URI select(String service);
}
