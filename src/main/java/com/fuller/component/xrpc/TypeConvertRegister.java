package com.fuller.component.xrpc;

import com.fuller.component.xrpc.convert.TypeConvert;

import java.lang.reflect.Type;

/**
 * 类型转换器注册器
 *
 * @author Allen Huang on 2022/2/23
 */
public interface TypeConvertRegister {

    /**
     * 判断此类型是否存在类型转换器。
     * 如果不存在不会给类型创建默认的转换器实例。
     *
     * @param type 目标类型
     * @return 如果存在，则返回true
     */
    boolean existsConvert(Type type);

    /**
     * 获取此类型的类型转换器实例。如果该类型不存在类型转换器，将会创建一个默认的类型转换器。
     *
     * @param type 需要做类型转换的类型
     * @return 返回转换器实例
     */
    TypeConvert getConvert(Type type);

    /**
     * 注册一个类型转换器，此方法是线程安全的。
     *
     * @param type    需要做类型转换的类型
     * @param convert 转换器实例
     */
    void registerConvert(Type type, TypeConvert convert);

}
