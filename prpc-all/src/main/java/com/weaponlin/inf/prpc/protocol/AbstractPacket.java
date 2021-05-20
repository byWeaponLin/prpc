package com.weaponlin.inf.prpc.protocol;

public abstract class AbstractPacket implements PPacket {

    private boolean heartbeat;

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Override
    public boolean isHeartbeat() {
        return heartbeat;
    }
}
