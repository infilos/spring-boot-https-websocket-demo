package com.infilos.usage;

import com.infilos.api.ClientReqMessage;
import com.infilos.client.AbstractClusterClient;
import com.infilos.client.ClusterClientConnector;
import com.infilos.utils.Network;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class CustomClusterClient extends AbstractClusterClient implements InitializingBean {
    private ClusterClientConnector clusterClientConnector;
    private final String clientId = UUID.randomUUID().toString().replaceAll("-", "");

    @Override
    public void afterPropertiesSet() throws Exception {
        String serverHost = Network.Localhost; // server runs locally
        clusterClientConnector = new ClusterClientConnector(this, clientId, serverHost);
        clusterClientConnector.connect();
    }

    @Scheduled(fixedDelayString = "PT20S", initialDelayString = "PT10S")
    public void publishReqMessagePeriodic() {
        publishReqMessage(ClientReqMessage.builder().clientId(clientId).build());
    }
}
