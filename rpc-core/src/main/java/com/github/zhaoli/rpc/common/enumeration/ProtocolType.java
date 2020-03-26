package com.github.zhaoli.rpc.common.enumeration;

import com.github.zhaoli.rpc.common.enumeration.support.ExtensionBaseType;
import com.github.zhaoli.rpc.protocol.http.HttpProtocol;
import com.github.zhaoli.rpc.protocol.injvm.InJvmProtocol;
import com.github.zhaoli.rpc.protocol.tcp.TcpProtocol;
import com.github.zhaoli.rpc.protocol.api.Protocol;
import com.github.zhaoli.rpc.protocol.api.support.AbstractProtocol;

/**
 * @author zhaoli
 * @date 2018/7/14
 */
public enum ProtocolType implements ExtensionBaseType<Protocol> {
    HTTP(new HttpProtocol()), INJVM(new InJvmProtocol()), TCP(new TcpProtocol());
    private AbstractProtocol protocol;

    ProtocolType(AbstractProtocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public Protocol getInstance() {
        return protocol;
    }
}
