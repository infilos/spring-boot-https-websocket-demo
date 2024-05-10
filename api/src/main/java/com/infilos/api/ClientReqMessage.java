package com.infilos.api;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientReqMessage extends WebsocketMessage {
    @Builder.Default
    private String content = ClientReqMessage.class.getSimpleName();
}
