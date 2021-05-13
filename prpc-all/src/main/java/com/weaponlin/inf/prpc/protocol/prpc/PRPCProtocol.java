package com.weaponlin.inf.prpc.protocol.prpc;

import com.weaponlin.inf.prpc.protocol.AbstractProtocol;
import com.weaponlin.inf.prpc.protocol.ProtocolType;

public class PRPCProtocol extends AbstractProtocol {



    @Override
    public ProtocolType getProtocol() {
        return ProtocolType.brpc;
    }
}
