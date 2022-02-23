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
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return Void.TYPE;
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 10;
    }
}
