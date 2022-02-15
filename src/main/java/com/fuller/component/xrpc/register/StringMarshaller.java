package com.fuller.component.xrpc.register;

import com.fuller.component.xrpc.exception.RpcException;
import io.grpc.MethodDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Allen Huang on 2022/2/11
 */
@Slf4j
public class StringMarshaller implements MethodDescriptor.Marshaller<String> {

    @Override
    public InputStream stream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String parse(InputStream stream) {
        try {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RpcException("解析字符串失败", e);
        } finally {
            try {
                stream.close();
            } catch (Exception e) {
                log.error("gRPC流关闭失败", e);
            }
        }
    }

}
