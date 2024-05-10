package com.infilos.server.config;

import com.infilos.api.Consts;
import com.infilos.server.service.ClusterServerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

/**
 * Execute during handshake between client and server.
 */
@Slf4j
@Component
public class WebSocketDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    @Autowired
    private ClusterServerService clusterServerService;

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                String clientId = session.getHandshakeHeaders().getFirst(Consts.CLIENT_ID_HEADER_NAME);

                if (StringUtils.isNotBlank(clientId)) {
                    log.info("Client connect succed: clientId={}", clientId);
                    clusterServerService.registerClient(clientId);
                    super.afterConnectionEstablished(session);
                }
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                String clientId = session.getHandshakeHeaders().getFirst(Consts.CLIENT_ID_HEADER_NAME);

                if (StringUtils.isNotBlank(clientId)) {
                    log.info("Client connect closed: clientId={}, status={}", clientId, closeStatus);
                    clusterServerService.unregisterClient(clientId);
                    super.afterConnectionClosed(session, closeStatus);
                }
            }
        };
    }
}
