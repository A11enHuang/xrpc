package com.fuller.component.xrpc.parser;

import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 方法解析器，主要用于解析方法中的请求参数类型。
 * 对于XRPC来说，参数处理的流程是:
 * <ul>
 *     <li>使用MethodParameterParser解析出方法参数的类型</li>
 *     <li>根据第一步中解析出的参数类型，解析出TypeConvert</li>
 *     <li>TypeConvert负责将Java参数转换成gRPC的参数</li>
 * </ul>
 *
 * @author Allen Huang on 2022/2/23
 */
public interface MethodParameterParser extends Ordered {

    /**
     * 解析某个方法的参数类型。可以使用此方法解析出的参数类型去获取TypeConvert实例
     *
     * @param method 需要解析的目标方法
     * @return 参数的类型
     */
    Type parse(Method method);

    /**
     * 将Java参数转换成gRPC参数后，交由TypeConvert做参数的转换，最终使用gRPC传输此参数
     *
     * @param method 执行的目标方法
     * @param args   参数列表
     * @return 转换成gRPC参数列表结果
     */
    Object toRpcValue(Method method, Object[] args);

    /**
     * 将TypeConvert转换后的结果转换成Java方法参数列表
     *
     * @param method   执行的目标方法
     * @param rpcValue 从gRPC参数转换成Java行参列表
     * @return Java行参列表
     */
    default List<Object> toMethodValue(Method method, Object rpcValue) {
        return Collections.singletonList(rpcValue);
    }

    /**
     * 此解析器是否支持此方法
     *
     * @param method 要执行的目标方法
     * @return 如果支持此方法，则返回true
     */
    boolean isSupport(Method method);

    @Override
    default int getOrder() {
        return 0;
    }

}
