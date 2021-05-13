package com.weaponlin.inf.prpc.protocol;

import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.dubbo.DubboProtocol;
import com.weaponlin.inf.prpc.protocol.prpc.PRPCProtocol;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

public class PProtocolFactory {

    public static PProtocol getProtocol(ProtocolType protocolType, String codec) {

        if (protocolType == ProtocolType.prpc) {
            return new PRPCProtocol(protocolType, codec);
        } else if (protocolType == ProtocolType.dubbo) {
            return new DubboProtocol(protocolType, codec);
        } else {
            throw new PRPCException("cant recognize protocol: " + protocolType);
        }
    }

    public static PProtocol getProtocol(String protocol, String codec) {
        ProtocolType protocolType = ProtocolType.getProtocolType(protocol);
        return getProtocol(protocolType, codec);
    }

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode
    private static class ProtocolGroup {
        private ProtocolType protocolType;
        private String codec;

    }
}
