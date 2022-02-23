package com.fuller.component.xrpc.test;

import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.XRPConfiguration;
import com.fuller.component.xrpc.annotation.XRPC;
import io.grpc.MethodDescriptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * @author Allen Huang on 2022/2/23
 */
@SpringBootTest(classes = XRPConfigurationTest.Config.class)
public class XRPConfigurationTest {

    @Autowired
    private XRPConfiguration xrpConfiguration;

    @Test
    public void test(){
        ServiceDefinition serviceDefinition = xrpConfiguration.parseServiceDefinition(DemoService.class);
        for (Method method : DemoService.class.getDeclaredMethods()) {
            MethodDescriptor methodDescriptor = xrpConfiguration.getMethodDescriptor(serviceDefinition, method);
            System.out.println(methodDescriptor);
        }
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {

    }

    @XRPC(hostname = "hostname")
    interface DemoService {
        void sayHello();
    }

}
