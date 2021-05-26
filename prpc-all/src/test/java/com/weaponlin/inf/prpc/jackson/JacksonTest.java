package com.weaponlin.inf.prpc.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weaponlin.inf.prpc.api.hello.HelloRequest;
import org.junit.Test;

import java.io.IOException;

public class JacksonTest {

    @Test
    public void to_string() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        final HelloRequest request = HelloRequest.builder().size(100).message("asdfkajsdlfj").build();
        System.out.println(objectMapper.writeValueAsString(request));
    }

    @Test
    public void to_object() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"size\":100,\"message\":\"asdfkajsdlfj\"}";
        final HelloRequest request = objectMapper.readValue(json, HelloRequest.class);
        System.out.println(request);
    }

    @Test
    public void to_bytes() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        final HelloRequest request = HelloRequest.builder().size(100).message("asdfkajsdlfj").build();
        final byte[] bytes = objectMapper.writeValueAsBytes(request);
        System.out.println(bytes);
        final HelloRequest helloRequest = objectMapper.readValue(bytes, HelloRequest.class);
        System.out.println(helloRequest);
    }
}
