package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.ServiceDefinition;

/**
 * @author Allen Huang on 2022/2/22
 */
public interface ConsumerContext {

    Object getProxy(ServiceDefinition definition);

}
