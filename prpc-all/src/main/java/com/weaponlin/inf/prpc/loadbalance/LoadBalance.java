package com.weaponlin.inf.prpc.loadbalance;

import com.weaponlin.inf.prpc.exception.PRpcException;
import com.weaponlin.inf.prpc.registry.AbstractRegistry;
import com.weaponlin.inf.prpc.remote.URI;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * TODO
 */
public interface LoadBalance {

    URI select(String service);

    default List<URI> getProviders(String service) {
        Set<URI> providers = AbstractRegistry.getProviders(service);
        if (CollectionUtils.isEmpty(providers)) {
            throw new PRpcException("there is no providers, please start server!");
        }
        return Lists.newArrayList(providers);
    }
}
