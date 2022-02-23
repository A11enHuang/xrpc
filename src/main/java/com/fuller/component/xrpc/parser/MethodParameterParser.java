package com.fuller.component.xrpc.parser;

import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Allen Huang on 2022/2/23
 */
public interface MethodParameterParser extends Ordered {

    Type parse(Method method);

    @Override
    default int getOrder() {
        return 0;
    }

}
