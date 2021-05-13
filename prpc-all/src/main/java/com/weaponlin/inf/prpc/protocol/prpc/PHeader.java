package com.weaponlin.inf.prpc.protocol.prpc;

import com.weaponlin.inf.prpc.constants.Constants;
import com.weaponlin.inf.prpc.exception.PRPCException;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class PHeader {
    public static final int HEAD_LEN = 12;

    private int magic = Constants.MAGIC;

    /**
     * 校验
     */
    private int metaSize;
    private int bodySize;

    public void encode(ByteBuf byteBuf) {
        byteBuf.writeInt(magic);
        byteBuf.writeInt(metaSize);
        byteBuf.writeInt(bodySize);
    }

    public static PHeader decode(ByteBuf byteBuf) {
        PHeader header = new PHeader();
        header.setMagic(byteBuf.readInt());
        header.setMetaSize(byteBuf.readInt());
        header.setBodySize(byteBuf.readInt());
        return header;
    }

    public void validate() {
        if (magic != Constants.MAGIC || metaSize <= 0 || bodySize <= 0) {
            throw new PRPCException("invalid protocol");
        }
    }
}
