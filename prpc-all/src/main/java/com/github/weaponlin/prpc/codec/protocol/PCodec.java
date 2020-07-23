package com.github.weaponlin.prpc.codec.protocol;

/**
 * encode and decode
 */
public interface PCodec<T> {

    byte[] encode(T t);

    T decode(byte[] bytes, T t);
}
