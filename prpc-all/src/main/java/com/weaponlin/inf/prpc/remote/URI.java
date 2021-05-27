package com.weaponlin.inf.prpc.remote;

import com.weaponlin.inf.prpc.exception.PRPCException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode
public class URI {

    private String host;

    private int port;

    private String codec;

    private String group;

    private String protocol;

    private String idc;

    public static URI newURI(String host, int port) {
        if (StringUtils.isBlank(host)) {
            throw new RuntimeException("host is blank, please check it");
        }
        if (port <= 0) {
            throw new RuntimeException("illegal port");
        }

        URI uri = new URI();
        uri.setHost(host);
        uri.setPort(port);
        return uri;
    }

    /**
     *
     * @param provider like: 127.0.0.1:8818?protocol=prpc&codec=protobuf&group=default
     * @return
     */
    public static URI newURI(String provider) {
        try {
            provider = URLDecoder.decode(provider, "UTF-8");
            String[] split = provider.split("\\?");
            String[] hosts = split[0].substring(7).split(":");
            URI uri = newURI(hosts[0], Integer.valueOf(hosts[1]));
            Map<String, String> params = Stream.of(split[1].split("&"))
                    .map(e -> e.split("="))
                    .collect(Collectors.toMap(a -> a[0], a -> a[1]));
            Optional.ofNullable(params.get("protocol"))
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(uri::setProtocol);

            Optional.ofNullable(params.get("group"))
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(uri::setGroup);

            Optional.ofNullable(params.get("codec"))
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(uri::setCodec);

            Optional.ofNullable(params.get("idc"))
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(uri::setIdc);

            System.out.println("idc: " + uri.getIdc());

            return uri;
        } catch (Exception e) {
            throw new PRPCException("analysis provider uri failed, uri: " + provider);
        }
    }
}
