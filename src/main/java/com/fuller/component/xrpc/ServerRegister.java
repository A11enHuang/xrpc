package com.fuller.component.xrpc;

/**
 * @author Allen Huang on 2022/2/23
 */
public interface ServerRegister {

    ServiceDefinition parseServiceDefinition(Class<?> type);

}
