package com.github.zhaoli.rpc.transport.tcp.client;

import io.netty.handler.timeout.IdleStateHandler;

public class ClientIdleCheckHandler extends IdleStateHandler {

    public ClientIdleCheckHandler() {
        super(0, 5, 0);
    }

}
