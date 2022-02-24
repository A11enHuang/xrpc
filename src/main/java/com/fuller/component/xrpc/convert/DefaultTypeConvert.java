package com.fuller.component.xrpc.convert;

import com.fuller.component.xrpc.util.ParameterizedTypeImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

/**
 * @author Allen Huang on 2022/2/23
 */
public class DefaultTypeConvert implements TypeConvert<Object> {

    protected final Type type;

    public DefaultTypeConvert(Type type) {
        this.type = new ParameterizedTypeImpl(new Type[]{type}, null, Wrapper.class);
    }

    @Override
    public Object convertFrom(Object obj) {
        if (obj == null) {
            return null;
        }
        return ((Wrapper) obj).getValue();
    }

    @Override
    public Object convertTo(Object request) {
        return new Wrapper<>(request);
    }

    @Override
    public Type getDataType() {
        return type;
    }

    @Override
    public boolean isStream() {
        return false;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Wrapper<T> {

        private T value;

    }


}
