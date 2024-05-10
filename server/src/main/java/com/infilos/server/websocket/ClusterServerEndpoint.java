package com.infilos.server.websocket;

import com.infilos.api.ClientAckMessage;
import com.infilos.api.ClientReqMessage;
import com.infilos.api.ServerAckMessage;
import com.infilos.api.WebsocketLogging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import static com.infilos.api.Consts.*;

@Slf4j
@RestController
public class ClusterServerEndpoint implements WebsocketLogging {

    @Autowired
    private SimpMessagingTemplate client;

    @MessageMapping(CLIENT_REQ_CONSUME_TOPIC)
    public void acceptClientReqMessage(@Payload ClientReqMessage clientReqMessage) {
        logAccepting(CLIENT_REQ_CONSUME_TOPIC, clientReqMessage);

        ServerAckMessage ackMessage = ServerAckMessage.builder().clientId(clientReqMessage.getClientId()).build();
        String ackTopic = SERVER_ACK_PUBLISH_TOPIC_PREFIX + clientReqMessage.getClientId();
        logSending(ackTopic, ackMessage);
        client.convertAndSend(ackTopic, ackMessage);
    }

    @MessageMapping(CLIENT_ACK_CONSUME_TOPIC)
    public void acceptClientAckMessage(@Payload ClientAckMessage clientAckMessage) {
        logAccepting(CLIENT_ACK_CONSUME_TOPIC, clientAckMessage);
    }
}
