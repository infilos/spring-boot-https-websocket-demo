package com.infilos.client;

import com.infilos.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.concurrent.atomic.AtomicReference;

import static com.infilos.api.Consts.*;

@Slf4j
public abstract class AbstractClusterClient implements WebsocketLogging {

    private final AtomicReference<StompSession> stompSession = new AtomicReference<>();

    void setupStompSession(StompSession stompSession) {
        this.stompSession.set(stompSession);
    }

    public void publishReqMessage(ClientReqMessage clientReqMessage) {
        logSending(CLIENT_REQ_PUBLISH_TOPIC, clientReqMessage);
        stompSession.get().send(CLIENT_REQ_PUBLISH_TOPIC, clientReqMessage);
    }

    public void consumeAckMessage(ServerAckMessage serverAckMessage) {
        logAccepting(SERVER_ACK_PUBLISH_TOPIC_PREFIX, serverAckMessage);
    }

    public void consumeReqMessage(ServerReqMessage serverReqMessage) {
        logAccepting(SERVER_REQ_PUBLISH_TOPIC_PREFIX, serverReqMessage);

        publishAckMessage(ClientAckMessage.builder().clientId(serverReqMessage.getClientId()).build());
    }

    public void publishAckMessage(ClientAckMessage clientAckMessage) {
        logSending(CLIENT_ACK_PUBLISH_TOPIC, clientAckMessage);
        stompSession.get().send(CLIENT_ACK_PUBLISH_TOPIC, clientAckMessage);
    }

    /**
     * 连接异常处理
     */
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable e) {
    }


    /**
     * 传输异常处理
     */
    public void handleTransportError(StompSession session, Throwable e) {
    }
}
