package com.github.zhaoli.rpc.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by zhaoli on 2017/7/31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message implements Serializable {
    private Header header;
    private byte[] body;

    public static Message PING = Message.builder()
            .header(new Header(MessageType.PING, 0, 0))
            .body(new byte[]{})
            .build();

    public static Message PONG = Message.builder()
            .header(new Header(MessageType.PONG, 0, 0))
            .body(new byte[]{})
            .build();

}
