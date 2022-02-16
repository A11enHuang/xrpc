package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Allen Huang on 2022/2/15
 */
@RequiredArgsConstructor
public abstract class BaseMethodParser implements MethodParser {

    protected final MarshallerRegister marshallerRegister;

    protected final Map<Method, Boolean> supportCache = new ConcurrentHashMap<>();

    @Override
    public MethodDescriptor parseDescriptor(ServiceDefinition definition, Method method) {
        if (isSupport(definition, method)) {
            Type requestType = getRequestType(definition, method);
            Type resultType = getResultType(definition, method);
            return MethodDescriptor.newBuilder()
                    .setType(getMethodType())
                    .setFullMethodName(definition.getServiceName() + "/" + method.getName())
                    .setSampledToLocalTracing(true)
                    .setRequestMarshaller(marshallerRegister.getMarshaller(requestType))
                    .setResponseMarshaller(marshallerRegister.getMarshaller(resultType))
                    .build();
        }
        return null;
    }

    @Override
    public ServerCallHandler parseServerCallHandler(Method method, Object target) {
        Boolean isSupport = Optional.ofNullable(supportCache.get(method)).orElse(Boolean.FALSE);
        if (isSupport) {
            return buildServerCallHandler(method, target);
        }
        return null;
    }

    protected boolean isSupport(ServiceDefinition definition, Method method) {
        return supportCache.computeIfAbsent(method, k -> this.checkMethod(definition, k));
    }

    /**
     * 创建ServerCallHandler实例
     *
     * @param method 目标方法
     * @param target 目标实例
     * @return 服务handler
     */
    protected abstract ServerCallHandler buildServerCallHandler(Method method, Object target);

    /**
     * 检查当前解析器是否支持此方法
     *
     * @param definition 服务定义
     * @param method     目标方法
     * @return 如果支持此方法则返回true
     */
    protected abstract boolean checkMethod(ServiceDefinition definition, Method method);

    /**
     * 当前解析器支持的方法类型
     *
     * @return 返回方法类型实例
     */
    protected abstract MethodDescriptor.MethodType getMethodType();

    /**
     * 获取当前方法的请求参数类型，根据此类型获取Marshaller实例
     *
     * @param definition 服务定义
     * @param method     目标方法
     * @return 返回请求参数类型
     */
    protected abstract Type getRequestType(ServiceDefinition definition, Method method);

    /**
     * 获取当前方法的返回值类型，根据此类型获取Marshaller实例
     *
     * @param definition 服务定义
     * @param method     目标方法
     * @return 返回值类型
     */
    protected Type getResultType(ServiceDefinition definition, Method method) {
        return method.getGenericReturnType();
    }

}
