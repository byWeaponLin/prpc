package com.weaponlin.inf.prpc.protocol.dubbo;

public class DubboConstants {
    public static final int FIXED_HEAD_LEN = 16;
    public static final short MAGIC = (short) 0xdabb;
    public static final byte FLAG_REQUEST = (byte) 0x80;
    public static final byte FLAG_RESPONSE = (byte) 0x40;
    public static final byte FLAG_HEARTBEAT = (byte) 0x20;
    public static final byte HESSIAN2_SERIALIZATION_ID = 2;
    public static final String DEFAULT_DUBBO_PROTOCOL_VERSION = "2.7.5";
    public static final byte RESPONSE_OK = 20;
    /**
     * service error.
     */
    public static final byte SERVICE_ERROR = 70;

    public static final byte RESPONSE_WITH_EXCEPTION = 0;
    public static final byte RESPONSE_VALUE = 1;
    public static final byte RESPONSE_NULL_VALUE = 2;
    public static final byte RESPONSE_WITH_EXCEPTION_WITH_ATTACHMENTS = 3;
    public static final byte RESPONSE_VALUE_WITH_ATTACHMENTS = 4;
    public static final byte RESPONSE_NULL_VALUE_WITH_ATTACHMENTS = 5;

    public static final String HEARTBEAT_EVENT = null;

    public static final int DEFAULT_OUTPUT_BUFFER_SIZE = 4096;
}
