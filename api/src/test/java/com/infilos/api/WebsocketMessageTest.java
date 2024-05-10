package com.infilos.api;

import com.infilos.relax.Json;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class WebsocketMessageTest extends Assertions {

    @Test
    public void test() {
        ServerReqMessage message = ServerReqMessage.builder().clientId("112233").build();
        String json = Json.from(message).asString();
        log.info("message json: {}", json);
        ServerReqMessage decoded = Json.from(json).asObject(ServerReqMessage.class);

        assertEquals(decoded, message);
    }
}