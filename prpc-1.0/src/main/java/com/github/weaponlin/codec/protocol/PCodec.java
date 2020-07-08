package com.github.weaponlin.codec.protocol;

/**
 * encode and decode
 */
public interface PCodec<T> {

    byte[] encode(T t);

    T decode(byte[] bytes, T t);
}
