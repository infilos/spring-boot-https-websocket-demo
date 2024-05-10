package com.infilos.client;

import com.infilos.api.Consts;
import com.infilos.api.ServerAckMessage;
import com.infilos.api.ServerReqMessage;
import com.infilos.api.WebsocketMessage;
import com.infilos.relax.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.infilos.api.Consts.SERVER_ACK_PUBLISH_TOPIC_PREFIX;
import static com.infilos.api.Consts.SERVER_REQ_PUBLISH_TOPIC_PREFIX;

@Slf4j
public class ClientStompSessionHandler extends StompSessionHandlerAdapter {

    private final WebSocketStompClient stompClient;
    private final AbstractClusterClient manageClient;
    private final String serverUrl;
    private final String clientId;
    private final AtomicBoolean isReconnecting = new AtomicBoolean(false);
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);

    public ClientStompSessionHandler(WebSocketStompClient stompClient,
                                     AbstractClusterClient manageClient,
                                     String serverUrl,
                                     String clientId) {
        super();
        this.stompClient = stompClient;
        this.serverUrl = serverUrl;
        this.manageClient = manageClient;
        this.clientId = clientId;
    }


    @Override
    public Type getPayloadType(StompHeaders headers) {
        return WebsocketMessage.class;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("Cluster client connected.");
        session.subscribe(SERVER_REQ_PUBLISH_TOPIC_PREFIX + clientId, this);
        session.subscribe(SERVER_ACK_PUBLISH_TOPIC_PREFIX + clientId, this);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (Objects.isNull(payload)) {
            log.info("Cluster client accept: null");
            return;
        }
        if (!(payload instanceof WebsocketMessage)) {
            log.info("Cluster client accept unknown type: {}, {}", payload.getClass().getName(), Json.from(payload).asString());
            return;
        }
        log.info("Cluster client accept: {}, {}", payload.getClass().getSimpleName(), Json.from(payload).asString());

        /**
         * 分发执行不同消息的处理逻辑
         */
        if (payload instanceof ServerReqMessage) {
            // 服务端请求
            manageClient.consumeReqMessage((ServerReqMessage) payload);
        } else if (payload instanceof ServerAckMessage) {
            // 服务端回复
            manageClient.consumeAckMessage((ServerAckMessage) payload);
        } else {
            // 无效消息
            log.info("Cluster client accept unsupport message: {}, {}", payload.getClass().getName(), Json.from(payload).asString());
        }
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable e) {
        //连接异常,处理异常
        manageClient.handleException(session, command, headers, payload, e);
        log.error("Cluster client error: {}", Objects.nonNull(e.getMessage()) ? e.getMessage() : "", e);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable e) {
        //传输异常，处理异常
        manageClient.handleTransportError(session, e);
        log.error("Cluster client transport error: {}", Objects.nonNull(e.getMessage()) ? e.getMessage() : "", e);

        log.error("Cluster client transport error: client is not connected, start reconnecting...");
        if (isReconnecting.compareAndSet(false, true)) {
            try {
                tryReestablishConnection();
            } catch (Exception ex) {
                log.error("Cluster client transport error: client reconnect failed!", ex);
            } finally {
                isReconnecting.set(false);
                log.error("Cluster client transport error: client reconnect completed!");
            }
        }

    }

    private void tryReestablishConnection() {
        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        handshakeHeaders.add(Consts.CLIENT_ID_HEADER_NAME, clientId);
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add(Consts.CLIENT_ID_HEADER_NAME, clientId);

        boolean disconnected = true;
        while (disconnected) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ignore) {
            }
            try {
                log.error("Cluster client start execute reconnecting({})...", reconnectAttempts.get());

                StompSession stompSession = stompClient.connect(
                        serverUrl,
                        handshakeHeaders,
                        connectHeaders,
                        this
                ).get();

                manageClient.setupStompSession(stompSession);
                disconnected = false;
                reconnectAttempts.set(0);

                log.error("Cluster client reconnecting succed.");
            } catch (Exception e) {
                log.error("Cluster client try reconnect failed.", e);
            } finally {
                reconnectAttempts.incrementAndGet();
            }
        }
    }
}
