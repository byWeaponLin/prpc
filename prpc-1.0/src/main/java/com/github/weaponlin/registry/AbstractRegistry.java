package com.github.weaponlin.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractRegistry implements Registry {

    private Map<Object, Object> services = new ConcurrentHashMap<>();

}
