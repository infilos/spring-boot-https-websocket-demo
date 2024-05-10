package com.infilos.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClientReqMessage.class),
        @JsonSubTypes.Type(value = ClientAckMessage.class),
        @JsonSubTypes.Type(value = ServerReqMessage.class),
        @JsonSubTypes.Type(value = ServerAckMessage.class)
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class WebsocketMessage {
    private String clientId;

    @JsonProperty
    public Long getTimestamp() {
        return System.currentTimeMillis();
    }

    @JsonProperty("@type")
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
