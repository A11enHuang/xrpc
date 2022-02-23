package com.fuller.component.xrpc.convert;

import java.lang.reflect.Type;

/**
 * 类型转换器，负责将gRPC数据传输对象转换成方法的参数.
 *
 * @author Allen Huang on 2022/2/23
 */
public interface TypeConvert<T> {

    /**
     * 将gRPC的数据传输参数转换成目标方法参数
     *
     * @param obj gRPC数据传输参数
     * @return 返回转换厚的目标方法参数
     */
    T convertFrom(Object obj);

    /**
     * 将目标方法参数转换成gRPC参数
     *
     * @param request 目标方法参数
     * @return gRPC参数
     */
    Object convertTo(T request);

    /**
     * 获取传输的数据类型
     */
    Type getDataType();

    /**
     * 是否是一个流数据
     */
    boolean isStream();

}
