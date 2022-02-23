package com.fuller.component.xrpc.convert;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;

/**
 * @author Allen Huang on 2022/2/23
 */
@RequiredArgsConstructor
public class DefaultTypeConvert implements TypeConvert<Object> {

    protected final Type type;

    @Override
    public Object convertFrom(Object obj) {
        return obj;
    }

    @Override
    public Object convertTo(Object request) {
        return request;
    }

    @Override
    public Type getDataType() {
        return type;
    }

    @Override
    public boolean isStream() {
        return false;
    }
}
