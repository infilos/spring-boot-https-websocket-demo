package com.infilos.api;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServerAckMessage extends WebsocketMessage {
    @Builder.Default
    private String content = ServerAckMessage.class.getSimpleName();
}
