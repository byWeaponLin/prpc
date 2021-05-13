package com.weaponlin.inf.prpc.codec;

import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import com.weaponlin.inf.prpc.protocol.prpc.PRequest;
import com.weaponlin.inf.prpc.protocol.prpc.PResponse;
import lombok.Getter;

@Getter
public class CodecPair {
    private PEncoder encoder;

    private PDecoder decoder;

    private CodecPair(PEncoder encoder, PDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public static CodecPair getServerCodec(ProtocolType protocolType, String codec) {
        if (protocolType == ProtocolType.prpc) {
            // TODO 缓存
            PDecoder decoder = new PDecoder(PRequest.class, codec, protocolType.name());
            PEncoder encoder = new PEncoder(PResponse.class, codec, protocolType.name());

            return new CodecPair(encoder, decoder);
        } else if (protocolType == ProtocolType.dubbo) {
            // TODO
            throw new UnsupportedOperationException("not support now");
        } else {
            throw new PRPCException("invalid protocol type: " + protocolType);
        }
    }
}
