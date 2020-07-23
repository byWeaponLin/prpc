package com.github.weaponlin.prpc.loadbalance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalanceFactory {

    private static Map<String, LoadBalance> loadBalanceMap = new ConcurrentHashMap<>();

    public static LoadBalance getLoadBalacer(String loadBalance) {
        if (!loadBalanceMap.containsKey(loadBalance)) {
            LoadBalance balance;
            switch (loadBalance) {
                case RoundRobinLoadBalance.NAME:
                    balance = new RoundRobinLoadBalance();
                    break;
                case TemporaryLoadBalance.NAME:
                    balance = new TemporaryLoadBalance();
                    break;
                case RandomLoadBalance.NAME:
                default:
                    balance = new RandomLoadBalance();
                    break;
            }
            loadBalanceMap.putIfAbsent(loadBalance, balance);
        }
        return loadBalanceMap.get(loadBalance);
    }
}
