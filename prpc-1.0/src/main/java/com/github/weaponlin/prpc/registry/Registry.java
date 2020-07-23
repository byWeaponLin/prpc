package com.github.weaponlin.prpc.registry;

public interface Registry {

    String PROVIDER_PATH = "/provider";

    String CONSUMER_PATH = "/consumer";

    void register();

    void unregister();

    void subscribe();

    void unsubscribe();

    @Deprecated
    void nodeChanged();

    void refresh();
}
