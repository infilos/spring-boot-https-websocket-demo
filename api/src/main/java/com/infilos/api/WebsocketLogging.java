package com.infilos.api;

import com.infilos.relax.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface WebsocketLogging {
    Logger LOGGER = LoggerFactory.getLogger(WebsocketLogging.class);

    /**
     * 发送日志
     *
     * @param topic
     * @param message
     */
    default void logSending(String topic, WebsocketMessage message) {
        LOGGER.info("Send to {}: {}", topic, Json.from(message).asString());
    }

    /**
     * 心跳日志
     *
     * @param topic
     * @param message
     */
    default void logAccepting(String topic, WebsocketMessage message) {
        LOGGER.info("Accept from {}: {}", topic, Json.from(message).asString());
    }

    /**
     * 心跳日志
     *
     * @param topic
     * @param session
     * @param message
     */
    default void logAccepting(String topic, String session, WebsocketMessage message) {
        LOGGER.info("Accept from {}-{}: {}", topic, session, Json.from(message).asString());
    }
}
