package com.weaponlin.inf.prpc.registry;

public interface Registry {

    String PROVIDER_PATH = "/provider";

    String CONSUMER_PATH = "/consumer";

    void register();

    void register(Class<?> service);

    void unregister();

    void subscribe();

    void subscribe(Class<?> service);

    void unsubscribe();

    @Deprecated
    void nodeChanged();

    void refresh();
}
