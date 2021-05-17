package com.weaponlin.inf.prpc.protocol.dubbo;

import com.weaponlin.inf.prpc.constants.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

@Data
public class DubboHeader {
    private short magic = Constants.DUBBO_MAGIC;
    private byte flag;
    private byte status;
    private long correlationId;
    private int bodyLength;

    public ByteBuf encode() {
        ByteBuf byteBuf = Unpooled.buffer(Constants.DUBBO_FIXED_HEAD_LEN);
        byteBuf.writeShort(Constants.DUBBO_MAGIC);
        byteBuf.writeByte(flag);
        byteBuf.writeByte(status);
        byteBuf.writeLong(correlationId);
        byteBuf.writeInt(bodyLength);
        return byteBuf;
    }

    public static DubboHeader decode(ByteBuf byteBuf) {
        DubboHeader header = new DubboHeader();
        header.setMagic(byteBuf.readShort());
        header.setFlag(byteBuf.readByte());
        header.setStatus(byteBuf.readByte());
        header.setCorrelationId(byteBuf.readLong());
        header.setBodyLength(byteBuf.readInt());
        return header;
    }
}
