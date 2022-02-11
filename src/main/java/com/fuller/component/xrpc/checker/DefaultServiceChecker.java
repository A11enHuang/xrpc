package com.fuller.component.xrpc.checker;

import com.fuller.component.xrpc.ServiceDefinition;

/**
 * 将检查bean定义.主要的检查内容如下:
 * <ul>
 *     <li>不支持重载方法</li>
 *     <li>一个服务全局只能定义一次，不能重复定义</li>
 * </ul>
 *
 * @author Allen Huang on 2022/2/11
 */
public class DefaultServiceChecker implements ServiceChecker {

    @Override
    public void check(ServiceDefinition definition) {
        Class<?> type = definition.getType();
        String appName = definition.getAppName();
        String serviceName = definition.getServiceName();

    }

}
