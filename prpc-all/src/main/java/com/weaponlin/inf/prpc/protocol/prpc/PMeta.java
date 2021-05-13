package com.weaponlin.inf.prpc.protocol.prpc;

import com.weaponlin.inf.prpc.exception.PRpcException;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Data
@Accessors(chain = true)
public class PMeta {

    private String requestId;

    private String serviceName;

    private String methodName;

    public void validate() {
        if (StringUtils.isBlank(requestId) || StringUtils.isBlank(serviceName)
                || StringUtils.isBlank(methodName)) {
            throw new PRpcException("invalid protocol");
        }
    }
}
