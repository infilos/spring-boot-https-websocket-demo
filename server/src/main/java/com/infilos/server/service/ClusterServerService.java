package com.infilos.server.service;

import com.infilos.api.ServerReqMessage;
import com.infilos.api.WebsocketLogging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static com.infilos.api.Consts.SERVER_REQ_PUBLISH_TOPIC_PREFIX;

@Slf4j
@Service
public class ClusterServerService implements WebsocketLogging {
    private static final ConcurrentHashMap<String, Object> CLIENT_REGISTRY = new ConcurrentHashMap<>();
    private static final Object VALUE_PLACEHOLDER = new Object();

    @Autowired
    private SimpMessagingTemplate client;

    public void registerClient(String clientId) {
        CLIENT_REGISTRY.put(clientId, VALUE_PLACEHOLDER);
    }

    public void unregisterClient(String clientId) {
        CLIENT_REGISTRY.remove(clientId);
    }

    public void sendServerReqMessage(String clientId) {
        String reqTopic = SERVER_REQ_PUBLISH_TOPIC_PREFIX + clientId;
        ServerReqMessage serverReqMessage = ServerReqMessage.builder().clientId(clientId).build();
        logSending(reqTopic, serverReqMessage);

        client.convertAndSend(reqTopic, serverReqMessage);
    }

    @Scheduled(fixedDelayString = "PT30S")
    public void publishReqMessagePeriodic() {
        CLIENT_REGISTRY.keySet().stream()
                .skip(CLIENT_REGISTRY.isEmpty() ? 0 : new Random().nextInt(CLIENT_REGISTRY.size()))
                .findFirst()
                .ifPresent(this::sendServerReqMessage);
    }
}
