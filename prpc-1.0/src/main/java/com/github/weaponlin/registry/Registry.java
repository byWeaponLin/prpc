package com.github.weaponlin.registry;

public interface Registry {

    String PROVIDER_PATH = "/provider";

    String CONSUMER_PATH = "/consumer";

    void register();

    void unregister();

    void subscribe();

    void unsubscribe();

    void nodeChanged();
}
