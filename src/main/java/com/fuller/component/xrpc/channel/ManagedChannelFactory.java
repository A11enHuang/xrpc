package com.fuller.component.xrpc.channel;

import io.grpc.ManagedChannel;

/**
 * @author Allen Huang on 2022/2/11
 */
public interface ManagedChannelFactory {

    //TODO: channel的应该需要重新设计
    ManagedChannel getManagedChannel(String host, int port);

}
