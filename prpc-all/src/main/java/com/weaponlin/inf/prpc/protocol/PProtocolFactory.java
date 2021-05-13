package com.weaponlin.inf.prpc.protocol;

import com.weaponlin.inf.prpc.exception.PRpcException;
import com.weaponlin.inf.prpc.protocol.dubbo.DubboProtocol;
import com.weaponlin.inf.prpc.protocol.prpc.PRPCProtocol;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

public class PProtocolFactory {

    private static Map<ProtocolGroup, Protocol> PROTOCOL_CACHE = new HashMap<>();

    public static Protocol getProtocol(ProtocolType protocolType, String codec) {
        ProtocolGroup protocolGroup = new ProtocolGroup().setProtocolType(protocolType)
                .setCodec(codec);

        if (PROTOCOL_CACHE.containsKey(protocolGroup)) {
            return PROTOCOL_CACHE.get(protocolGroup);
        }

        if (protocolType == ProtocolType.prpc) {
            PRPCProtocol prpcProtocol = new PRPCProtocol(protocolType, codec);
            PROTOCOL_CACHE.put(protocolGroup, prpcProtocol);
            return prpcProtocol;
        } else if (protocolType == ProtocolType.dubbo) {
            DubboProtocol dubboProtocol = new DubboProtocol(protocolType, codec);
            PROTOCOL_CACHE.put(protocolGroup, dubboProtocol);
            return dubboProtocol;
        } else {
            throw new PRpcException("cant recognize protocol: " + protocolType);
        }
    }

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode
    private static class ProtocolGroup {
        private ProtocolType protocolType;
        private String codec;

    }
}
