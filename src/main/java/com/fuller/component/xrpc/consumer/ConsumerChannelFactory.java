package com.fuller.component.xrpc.consumer;

import io.grpc.Channel;

/**
 * @author Allen Huang on 2022/2/11
 */
public interface ConsumerChannelFactory {

    //TODO: channel的应该需要重新设计
    Channel getChannel(String host, int port);

}
