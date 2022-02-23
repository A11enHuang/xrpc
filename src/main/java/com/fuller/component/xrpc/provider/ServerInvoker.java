package com.fuller.component.xrpc.provider;

import io.grpc.ServerCallHandler;

/**
 * @author Allen Huang on 2022/2/23
 */
public interface ServerInvoker {

    ServerCallHandler getServerCallHandler();

}
