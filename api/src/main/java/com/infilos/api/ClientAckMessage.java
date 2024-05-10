package com.infilos.api;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientAckMessage extends WebsocketMessage {
    @Builder.Default
    private String content = ClientAckMessage.class.getSimpleName();
}
