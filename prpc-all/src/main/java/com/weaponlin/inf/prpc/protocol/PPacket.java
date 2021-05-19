package com.weaponlin.inf.prpc.protocol;

import com.weaponlin.inf.prpc.protocol.prpc.PMeta;

public interface PPacket {

    PMeta getMeta();

    boolean isHeartbeat();
}
