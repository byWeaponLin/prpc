package com.github.weaponlin.codec;

import com.github.weaponlin.codec.protocol.PCodec;
import com.github.weaponlin.codec.protocol.dubbo.DubboCodec;
import com.github.weaponlin.codec.protocol.json.JsonCodec;
import com.github.weaponlin.codec.protocol.protobuf.ProtobufCodec;
import com.github.weaponlin.codec.protocol.prpc.PRpcCodec;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class CodecFactory {
    private CodecFactory() {
    }

    /**
     * @param protocolType protocol type {@link ProtocolGather}
     * @return if type is not exist then return default protocol -- Protobuf
     */
    public static PCodec getCodec(String protocolType) {
        final String protocolName = Optional.ofNullable(protocolType).map(String::toLowerCase)
                .orElse(ProtocolGather.PROTOBUF.type);
        return ProtocolGather.ProtocolMap.get(protocolName);
    }

    @Getter
    @AllArgsConstructor
    enum ProtocolGather {

        PROTOBUF("protobuf", new ProtobufCodec()),
        JSON("json", new JsonCodec()),
        DUBBO("dubbo", new DubboCodec()),
        PRPC("prpc", new PRpcCodec()),

        ;

        private String type;

        private PCodec pCodec;

        static Map<String, PCodec> ProtocolMap = Stream.of(ProtocolGather.values())
                .collect(toMap(ProtocolGather::getType, ProtocolGather::getPCodec));
    }
}
