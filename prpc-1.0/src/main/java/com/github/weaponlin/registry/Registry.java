package com.github.weaponlin.registry;

public interface Registry {

    void register();

    void unregister();

    void subscribe();

    void unsubscribe();

    void nodeChanged();
}
