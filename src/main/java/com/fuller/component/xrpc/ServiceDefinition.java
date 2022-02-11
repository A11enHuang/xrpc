package com.fuller.component.xrpc;

import lombok.Data;

/**
 * @author Allen Huang on 2022/2/11
 */
@Data
public class ServiceDefinition {

    /**
     * 服务的目标类型
     */
    private Class<?> type;

    /**
     * 服务的端口号
     */
    private int port;

    /**
     * 服务的应用名
     */
    private String appName;

    /**
     * 当前资源名
     */
    private String serviceName;

    /**
     * 版本号，预留字段
     */
    private String version;

}
