package com.weaponlin.inf.prpc.protocol.dubbo;

import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.weaponlin.inf.prpc.exception.PRPCException;
import com.weaponlin.inf.prpc.protocol.AbstractProtocol;
import com.weaponlin.inf.prpc.protocol.ProtocolType;
import com.weaponlin.inf.prpc.protocol.PPacket;
import com.weaponlin.inf.prpc.protocol.prpc.PResponse;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * TODO
 */
@Slf4j
public class DubboProtocol extends AbstractProtocol {

    private DubboRequest request;

    private DubboResponse response;

    public DubboProtocol(ProtocolType protocolType, String codec) {
        super(protocolType, codec);
    }

    @Override
    public ProtocolType getProtocol() {
        return ProtocolType.dubbo;
    }

    @Override
    public String getServiceName() {
        return request.getPath();
    }

    @Override
    public String getMethodName() {
        return request.getMethodName();
    }

    @Override
    public void encode(ByteBuf byteBuf, Object msg) {
        try {
            PResponse res = (PResponse) msg;

            DubboHeader dubboHeader = new DubboHeader();
            dubboHeader.setCorrelationId(Long.valueOf(res.getRequestId()));
            dubboHeader.setStatus(DubboConstants.RESPONSE_OK);
            byte[] bodyBytes = null;
            if (res.isHeartbeat()) {
                dubboHeader.setFlag(DubboConstants.FLAG_HEARTBEAT);
                bodyBytes = encodeHeartbeatBody();
            } else {
                dubboHeader.setFlag(DubboConstants.FLAG_RESPONSE);
                DubboResponse responseBody = new DubboResponse();
                responseBody.setResult(res.getResult());
                responseBody.setResponseType(DubboConstants.RESPONSE_VALUE);
                bodyBytes = encode(responseBody);
            }

            dubboHeader.setBodyLength(bodyBytes.length);
            dubboHeader.encode(byteBuf);
            byteBuf.writeBytes(bodyBytes);
        } catch (Exception e) {
            throw new PRPCException("encode dubbo request/response failed", e);
        }
    }

    private byte[] encodeHeartbeatBody() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        hessian2Output.writeString(DubboConstants.HEARTBEAT_EVENT);
        hessian2Output.flushBuffer();
        byte[] bodyBytes = outputStream.toByteArray();
        return bodyBytes;
    }

    @Override
    public void decode(ByteBuf byteBuf, Object msg) {
        PPacket packet = (PPacket) msg;
        if (packet.isHeartbeat()) {
            this.request = (DubboRequest) msg;
            this.request.setHeartbeat(true);
            return;
        }
        ByteBuf headerBuf = byteBuf.readBytes(DubboConstants.FIXED_HEAD_LEN);
        DubboHeader header = DubboHeader.decode(headerBuf);
        if (header.getMagic() != DubboConstants.MAGIC) {
            throw new PRPCException("dubbo protocol magic num is invalid, magic num: " + header.getMagic());
        }
        if ((header.getFlag() & DubboConstants.FLAG_HEARTBEAT) != 0) {
            // 心跳检测
            if (header.getBodyLength() <= 1) {
                this.request = (DubboRequest) msg;
                this.request.setHeartbeat(true);
            } else {
                throw new PRPCException("request body not null for event");
            }
        } else {
            byte[] bodyBytes = new byte[header.getBodyLength()];
            byteBuf.readBytes(bodyBytes);

            msg = decode(bodyBytes, msg);
            if (msg instanceof DubboRequest) {
                this.request = (DubboRequest) msg;
                this.request.setInvokeId(header.getCorrelationId());

            } else {
                throw new PRPCException("invalid dubbo request body");
            }
        }
    }

    @Override
    public PPacket getPacket(Class<?> clazz) {
        if (clazz == DubboRequest.class) {
            // 改为PRequest
//            return new PRequest().builder().serviceName(request.getPath())
//                    .methodName(request.getMethodName())
//                    .params(request.getArguments())
//                    .parameterTypes(request.getParameterTypes())
//                    .build();
            return request;
        } else if (clazz == PResponse.class) {
            // 写死responseType
            return new DubboResponse().setResponseType((byte) 1)
                    .setResult(response.getResult());
        } else {
            throw new PRPCException("invalid packet class: " + clazz);
        }
    }
}
