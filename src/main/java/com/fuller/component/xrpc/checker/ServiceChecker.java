package com.fuller.component.xrpc.checker;

import com.fuller.component.xrpc.ServiceDefinition;

/**
 * 服务定义检查器
 *
 * @author Allen Huang on 2022/2/11
 */
public interface ServiceChecker {

    /**
     * 检查服务的定义
     *
     * @param definition 需要检查的目标服务
     */
    void check(ServiceDefinition definition);

}
