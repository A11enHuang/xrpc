package com.fuller.component.xrpc;

import com.fuller.component.xrpc.annotation.XRPC;
import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Optional;

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
    private String hostname;

    /**
     * 当前资源名
     */
    private String serviceName;

    /**
     * 版本号，预留字段
     */
    private String version;

    public static ServiceDefinition build(Class<?> type, Environment environment) {
        XRPC xrpc = type.getAnnotation(XRPC.class);
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setHostname(resolvePlaceholders(xrpc.hostname(), environment));
        serviceDefinition.setPort(xrpc.port());
        serviceDefinition.setVersion(resolvePlaceholders(xrpc.version(), environment));
        String serviceName = Optional.ofNullable(xrpc.serviceName())
                .filter(StringUtils::hasText)
                .map(environment::resolvePlaceholders)
                .orElseGet(type::getSimpleName);
        String pkg = xrpc.servicePackage();
        if (StringUtils.hasText(pkg)) {
            serviceName = String.join(".", pkg, serviceName);
        }
        serviceDefinition.setServiceName(serviceName);
        serviceDefinition.setType(type);
        return serviceDefinition;
    }

    private static String resolvePlaceholders(String value, Environment environment) {
        return environment.resolvePlaceholders(value);
    }

}
