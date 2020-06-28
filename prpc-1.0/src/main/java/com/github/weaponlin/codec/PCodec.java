package com.github.weaponlin.codec;

import com.github.weaponlin.codec.schema.Schema;

/**
 * TODO codec and deserialize
 */
public interface PCodec {

    byte[] encode(Object object, Schema schema);

    Object decode(byte[] bytes);
}
