package com.fuller.component.xrpc.marshaller;

import io.grpc.MethodDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;

/**
 * @author Allen Huang on 2022/2/11
 */
@Slf4j
public class VoidMarshaller implements MethodDescriptor.Marshaller<Void> {

    private static Void INSTANCE;

    static {
        Constructor<?>[] constructors = Void.class.getDeclaredConstructors();
        Constructor constructor = constructors[0];
        constructor.setAccessible(true);
        try {
            INSTANCE = (Void) constructor.newInstance();
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public InputStream stream(Void value) {
        return new ByteArrayInputStream(new byte[]{});
    }

    @Override
    public Void parse(InputStream stream) {
        return INSTANCE;
    }

}
