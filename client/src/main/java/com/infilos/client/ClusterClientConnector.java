package com.infilos.client;

import com.infilos.api.Consts;
import com.infilos.relax.Json;
import com.infilos.utils.Resource;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.util.KeyStoreUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.JettyXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClusterClientConnector {
    private static final String KEYSTORE_PATH = "/ssl/spring_https.jks";
    private static final AllAllowedTrustManager[] ALL_ALLOWED_TRUST_MANAGERS = new AllAllowedTrustManager[]{
            new AllAllowedTrustManager()
    };

    private final AbstractClusterClient clusterClient;
    private final String clientId;
    private final String serverUrl;

    /**
     * ClusterManageClientBuilder
     */
    public ClusterClientConnector(AbstractClusterClient clusterClient, String clientId, String serverHost) {
        this.clusterClient = clusterClient;
        this.clientId = clientId;
        this.serverUrl = "https://" + serverHost + "/wss-endpoint";
    }

    /**
     * build
     */
    public void connect() throws Exception {
        StompSession stompSession = buildStompSession();
        clusterClient.setupStompSession(stompSession);
    }

    /**
     * buildStompSession
     */
    private StompSession buildStompSession() throws Exception {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.initialize();

        InputStream keystoreInputStream =  Resource.readAsStream(KEYSTORE_PATH);
        KeyStore keyStore = KeyStoreUtils.loadKeyStore(keystoreInputStream, "infilos@ssl".toCharArray(), "jks");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "infilos@ssl".toCharArray());

        SslContextFactory sslContextFactory = new SslContextFactory.Client(true);
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(keyManagerFactory.getKeyManagers(), ALL_ALLOWED_TRUST_MANAGERS, null);
        sslContextFactory.setSslContext(sslContext);
        // https://stackoverflow.com/questions/27324954/jetty-websocket-connection-ignore-self-signed-certs

        // or trust all
        // SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        // sslContextFactory.setTrustAll(true);
        // sslContextFactory.setExcludeCipherSuites();

        HttpClient http = new HttpClient(sslContextFactory);
        WebSocketClient client = new WebSocketClient(http);
        JettyWebSocketClient webSocketClient = new JettyWebSocketClient(client);
        client.start();

        List<Transport> transports = new ArrayList<>();
        transports.add(new JettyXhrTransport(http));
        transports.add(new WebSocketTransport(webSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(Json.underMapper());
        stompClient.setMessageConverter(messageConverter);
        stompClient.setDefaultHeartbeat(new long[]{10000, 10000});
        stompClient.setTaskScheduler(taskScheduler);

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        handshakeHeaders.add(Consts.CLIENT_ID_HEADER_NAME, clientId);
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add(Consts.CLIENT_ID_HEADER_NAME, clientId);
        //连接客户端
        return stompClient.connect(
                serverUrl,
                handshakeHeaders,
                connectHeaders,
                new ClientStompSessionHandler(stompClient, clusterClient, serverUrl, clientId)
        ).get(10, TimeUnit.SECONDS);
    }
}
