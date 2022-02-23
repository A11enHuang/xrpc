package com.fuller.component.xrpc;

import com.fuller.component.xrpc.convert.TypeConvert;

import java.lang.reflect.Type;

/**
 * @author Allen Huang on 2022/2/23
 */
public interface TypeConvertRegister {

    TypeConvert getConvert(Type type);

}
