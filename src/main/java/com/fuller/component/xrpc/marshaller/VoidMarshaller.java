package com.fuller.component.xrpc.marshaller;

import io.grpc.MethodDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Allen Huang on 2022/2/11
 */
@Slf4j
public class VoidMarshaller implements MethodDescriptor.Marshaller<Void> {

    @Override
    public InputStream stream(Void value) {
        return new ByteArrayInputStream(new byte[]{'a'});
    }

    @Override
    public Void parse(InputStream stream) {
        return null;
    }
    
}
