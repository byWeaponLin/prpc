package com.weaponlin.inf.prpc.protocol.dubbo;

import com.weaponlin.inf.prpc.protocol.AbstractProtocol;
import com.weaponlin.inf.prpc.protocol.ProtocolType;

public class DubboProtocol extends AbstractProtocol {

    private DubboRequest request;

    private DubboResponse response;

    @Override
    public ProtocolType getProtocol() {
        return ProtocolType.dubbo;
    }
}
