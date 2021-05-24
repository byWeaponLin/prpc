package com.weaponlin.inf.prpc.protocol.dubbo;

import com.weaponlin.inf.prpc.protocol.AbstractPacket;
import com.weaponlin.inf.prpc.protocol.prpc.PMeta;
import lombok.Data;
import lombok.experimental.Accessors;

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



    @Override
    public PMeta getMeta() {
        return new PMeta();
    }

    @Override
    public String getGroup() {
        return null;
    }
}
