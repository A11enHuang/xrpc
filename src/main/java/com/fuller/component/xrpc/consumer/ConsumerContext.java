package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.ServiceDefinition;

/**
 * @author Allen Huang on 2022/2/23
 */
public interface ConsumerContext {

    <T> T getProxy(ServiceDefinition definition);

}
