package com.weaponlin.inf.prpc.codec;

import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import com.weaponlin.inf.prpc.protocol.dubbo.DubboRequest;
import com.weaponlin.inf.prpc.protocol.dubbo.DubboResponse;
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
            PEncoder encoder = new PEncoder(PResponse.class, codec, protocolType.name());
            PDecoder decoder = new PDecoder(PRequest.class, codec, protocolType.name());

            return new CodecPair(encoder, decoder);
        } else if (protocolType == ProtocolType.dubbo) {
            PEncoder encoder = new PEncoder(DubboResponse.class, codec, protocolType.name());
            PDecoder decoder = new PDecoder(DubboRequest.class, codec, protocolType.name());
            return new CodecPair(encoder, decoder);
        } else {
            throw new PRPCException("invalid protocol type: " + protocolType);
        }
    }
}
