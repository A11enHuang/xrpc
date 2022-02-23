package com.fuller.component.xrpc;

import io.grpc.ServiceDescriptor;

/**
 * @author Allen Huang on 2022/2/23
 */
public interface ServerRegister {

    ServiceDefinition parseServiceDefinition(Class<?> type);

    ServiceDescriptor parseServiceDescriptor(Class<?> type);

}
