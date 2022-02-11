package com.fuller.component.xrpc.register;

import com.fuller.component.xrpc.ServiceDefinition;
import io.grpc.ServiceDescriptor;

/**
 * @author Allen Huang on 2022/2/11
 */
public interface ServiceRegister {

    /**
     * 解析服务描述
     *
     * @param definition 服务定义
     * @return 返回描述信息
     */
    ServiceDescriptor getServiceDescriptor(ServiceDefinition definition);

}
