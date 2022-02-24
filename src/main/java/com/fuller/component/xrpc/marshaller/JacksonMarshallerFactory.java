package com.fuller.component.xrpc.marshaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuller.component.xrpc.exception.RpcException;
import io.grpc.MethodDescriptor;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Jackson实现的JSON类型序列化器
 *
 * @author Allen Huang on 2022/2/11
 */
@RequiredArgsConstructor
public class JacksonMarshallerFactory implements MarshallerFactory {

    private final ObjectMapper objectMapper;

    @Override
    public MethodDescriptor.Marshaller getMarshaller(Type type) {
        return new JacksonMarshaller(type, objectMapper);
    }

    @RequiredArgsConstructor
    public static class JacksonMarshaller implements MethodDescriptor.Marshaller {

        private final Type type;

        private final ObjectMapper objectMapper;

        @Override
        public InputStream stream(Object obj) {
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(obj);
                return new ByteArrayInputStream(bytes);
            } catch (JsonProcessingException e) {
                throw new RpcException("序列化参数失败.", e);
            }
        }

        @Override
        public Object parse(InputStream inputStream) {
            try {
                return objectMapper.readValue(inputStream, new TypeReference<>() {
                    @Override
                    public Type getType() {
                        return type;
                    }
                });
            } catch (IOException e) {
                throw new RpcException("反序列化RPC参数失败", e);
            }
        }

    }

}
