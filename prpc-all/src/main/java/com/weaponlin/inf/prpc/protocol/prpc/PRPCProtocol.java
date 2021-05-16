package com.weaponlin.inf.prpc.protocol.prpc;

import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.AbstractProtocol;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class PRPCProtocol extends AbstractProtocol {

    private PHeader header;

    private PMeta meta;

    private PRequest request;

    private PResponse response;

    public PRPCProtocol(ProtocolType protocolType, String codec) {
        super(protocolType, codec);
    }

    @Override
    public ProtocolType getProtocol() {
        return ProtocolType.prpc;
    }

    @Override
    public String getServiceName() {
        return meta.getServiceName();
    }

    @Override
    public String getMethodName() {
        return meta.getMethodName();
    }

    @Override
    public void encode(ByteBuf byteBuf, Object msg) {
        if (msg instanceof PPacket) {
            PPacket packet = (PPacket) msg;
            PMeta meta = packet.getMeta();
            // meta
            byte[] metaBytes = encode(meta);
            // body
            byte[] bodyBytes = encode(packet);

            // header
            PHeader head = new PHeader();
            head.setMetaSize(metaBytes.length);
            head.setBodySize(bodyBytes.length);
            head.encode(byteBuf);

            byteBuf.writeBytes(metaBytes);
            byteBuf.writeBytes(bodyBytes);
        } else {
            throw new PRPCException("invalid protocol");
        }
    }

    @Override
    public void decode(ByteBuf byteBuf, Object msg) {
        try {
            // header
            ByteBuf headByteBuf = byteBuf.readBytes(PHeader.HEAD_LEN);
            this.header = PHeader.decode(headByteBuf);
            this.header.validate();

            // meta
            byte[] metaBytes = new byte[this.header.getMetaSize()];
            byteBuf.readBytes(metaBytes);
            this.meta = new PMeta();
            meta = (PMeta) decode(metaBytes, this.meta);
            this.meta.validate();

            // TODO json 反序列化失败了
            // body
            byte[] bodyBytes = new byte[this.header.getBodySize()];
            byteBuf.readBytes(bodyBytes);
            msg = decode(bodyBytes, msg);
            if (msg instanceof PRequest) {
                this.request = (PRequest) msg;
            } else if (msg instanceof PResponse) {
                this.response = (PResponse) msg;
            }
        } catch (Throwable e) {
            // TODO return e to client
            throw new PRPCException("cant deserialize request, class: " + msg.getClass().getName(), e);
        }
    }

    @Override
    public PPacket getPacket(Class<?> clazz) {
        if (clazz == PRequest.class) {
            return request;
        } else if (clazz == PResponse.class) {
            return response;
        } else {
            throw new PRPCException("invalid packet class: " + clazz);
        }
    }
}
