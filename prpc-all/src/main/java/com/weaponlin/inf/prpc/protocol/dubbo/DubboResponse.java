package com.weaponlin.inf.prpc.protocol.dubbo;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.weaponlin.inf.prpc.constants.Constants;
import com.weaponlin.inf.prpc.protocol.AbstractPacket;
import com.weaponlin.inf.prpc.protocol.prpc.PMeta;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * TODO
 */
@Data
@Accessors(chain = true)
public class DubboResponse extends AbstractPacket {
    private byte responseType;
    private Object result = null;
    private Throwable exception = null;
    private Map<String, String> attachments;

    public byte[] encodeResponseBody() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Constants.DEFAULT_DUBBO_OUTPUT_BUFFER_SIZE);
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        hessian2Output.setSerializerFactory(DubboRequest.SERIALIZER_FACTORY);
        hessian2Output.writeInt(responseType);
        hessian2Output.writeObject(result);
        if (attachments != null && attachments.size() > 0) {
            hessian2Output.writeObject(attachments);
        }
        hessian2Output.flush();
        return outputStream.toByteArray();
    }

    public static byte[] encodeErrorResponseBody(String errorMessage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        hessian2Output.setSerializerFactory(DubboRequest.SERIALIZER_FACTORY);
        hessian2Output.writeString(errorMessage);
        hessian2Output.flush();
        byte[] bodyBytes = outputStream.toByteArray();
        return bodyBytes;
    }

    public static byte[] encodeHeartbeatResponseBody() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        hessian2Output.setSerializerFactory(DubboRequest.SERIALIZER_FACTORY);
        hessian2Output.writeString(DubboConstants.HEARTBEAT_EVENT);
        hessian2Output.flush();
        byte[] bodyBytes = outputStream.toByteArray();
        return bodyBytes;
    }

    public static DubboResponse decodeResponseBody(ByteBuf responseBodyBuf) throws IOException {
        ByteBufInputStream inputStream = null;
        try {
            inputStream = new ByteBufInputStream(responseBodyBuf, true);
            Hessian2Input hessian2Input = new Hessian2Input(inputStream);
            hessian2Input.setSerializerFactory(DubboRequest.SERIALIZER_FACTORY);
            DubboResponse responseBody = new DubboResponse();
            responseBody.setResponseType((byte) hessian2Input.readInt());
            switch (responseBody.getResponseType()) {
                case DubboConstants.RESPONSE_NULL_VALUE:
                    break;
                case DubboConstants.RESPONSE_VALUE:
                    // TODO: add response class
                    responseBody.setResult(hessian2Input.readObject());
                    break;
                case DubboConstants.RESPONSE_WITH_EXCEPTION:
                    responseBody.setException((Throwable) hessian2Input.readObject());
                    break;
                case DubboConstants.RESPONSE_NULL_VALUE_WITH_ATTACHMENTS: {
                    Map<String, String> map = (Map<String, String>) hessian2Input.readObject(Map.class);
                    if (map != null && map.size() > 0) {
                        responseBody.setAttachments(map);
                    }
                    break;
                }
                case DubboConstants.RESPONSE_VALUE_WITH_ATTACHMENTS: {
                    // TODO: add response class
                    responseBody.setResult(hessian2Input.readObject());
                    Map<String, String> map = (Map<String, String>) hessian2Input.readObject(Map.class);
                    if (map != null && map.size() > 0) {
                        responseBody.setAttachments(map);
                    }
                    break;
                }
                case DubboConstants.RESPONSE_WITH_EXCEPTION_WITH_ATTACHMENTS: {
                    responseBody.setException((Throwable) hessian2Input.readObject());
                    Map<String, String> map = (Map<String, String>) hessian2Input.readObject(Map.class);
                    if (map != null && map.size() > 0) {
                        responseBody.setAttachments(map);
                    }
                    break;
                }
            }
            return responseBody;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Override
    public PMeta getMeta() {
        return new PMeta();
    }
}
