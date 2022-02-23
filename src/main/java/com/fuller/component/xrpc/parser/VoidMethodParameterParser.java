package com.fuller.component.xrpc.parser;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * @author Allen Huang on 2022/2/23
 */
@Component
public class VoidMethodParameterParser implements MethodParameterParser {

    @Override
    public Type parse(Method method) {
        return Void.TYPE;
    }

    @Override
    public Object parseValue(Method method, Object[] args) {
        return null;
    }

    @Override
    public boolean isSupport(Method method) {
        Parameter[] parameters = method.getParameters();
        return parameters.length == 0;
    }


    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 10;
    }
}
