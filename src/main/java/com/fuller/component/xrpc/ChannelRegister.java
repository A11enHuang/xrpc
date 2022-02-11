package com.fuller.component.xrpc;

import io.grpc.ManagedChannel;

/**
 * @author Allen Huang on 2022/2/11
 */
public interface ChannelRegister {

    ManagedChannel getManagedChannel(String host, int port);

}
