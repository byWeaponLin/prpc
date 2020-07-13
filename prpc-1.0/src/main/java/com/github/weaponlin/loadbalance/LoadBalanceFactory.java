package com.github.weaponlin.loadbalance;

public class LoadBalanceFactory {

    public static LoadBalance getLoadBalacer(String loadBalance) {
        switch (loadBalance) {
            case RandomLoadBalance.name:
                return new RandomLoadBalance();
            case RoundRobinLoadBalance.name:
                return new RoundRobinLoadBalance();
            case TemporaryLoadBalance.name:
                return new TemporaryLoadBalance();
            default:
                return new RandomLoadBalance();
        }
    }
}
