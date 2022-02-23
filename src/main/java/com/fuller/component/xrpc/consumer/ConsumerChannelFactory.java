package com.fuller.component.xrpc.consumer;

import io.grpc.Channel;

/**
 * @author Allen Huang on 2022/2/23
 */
public interface ConsumerChannelFactory {

    Channel getChannel(String host, int port);

}
