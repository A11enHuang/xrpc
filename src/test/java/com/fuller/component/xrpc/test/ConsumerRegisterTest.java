package com.fuller.component.xrpc.test;

import com.fuller.component.xrpc.MarshallerRegister;
import com.fuller.component.xrpc.annotation.XRPC;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.ClientCalls;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * @author Allen Huang on 2022/2/11
 */
@SpringBootTest(classes = ConsumerRegisterTest.Config.class)
public class ConsumerRegisterTest {

    @Autowired
    private DemoService demoService;

    @Autowired
    private MarshallerRegister marshallerRegister;

    @Test
    public void registerTest() {
        System.out.println(demoService);
    }

    @Test
    public void clientTest(){
        ManagedChannel channel = NettyChannelBuilder.forAddress("127.0.0.1", 8001)
                .usePlaintext()
                .build();
        AbstractStub.StubFactory factory = (channel1, callOptions) -> new AbstractBlockingStub(channel1,callOptions){
            @Override
            protected AbstractStub build(Channel channel, CallOptions callOptions) {
                return null;
            }
        };

        AbstractStub stub = AbstractBlockingStub.newStub(factory, channel);
        Class<DemoService> target = DemoService.class;
        Method method = target.getDeclaredMethods()[0];
        MethodDescriptor methodDescriptor = MethodDescriptor.newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName("DemoService" + "/" + method.getName())
                .setSampledToLocalTracing(true)
                .setRequestMarshaller(marshallerRegister.getMarshaller(String.class))
                .setResponseMarshaller(marshallerRegister.getMarshaller(method.getGenericReturnType()))
                .build();
        Object result = ClientCalls.blockingUnaryCall(stub.getChannel(), methodDescriptor, stub.getCallOptions(), "test");
        System.out.println("--------------" + result);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {

        @Bean
        public DemoService demoService() {
            return new DemoServiceImpl();
        }

    }

    @XRPC(appName = "service-demo.microservice")
    public interface DemoService {

        String say(String hello);

    }

    @XRPC(appName = "service-demo.microservice")
    public interface CarService {

        void run();

    }

    public static class DemoServiceImpl implements DemoService, CarService {

        @Override
        public String say(String hello) {
            return "hello world!" + hello;
        }

        @Override
        public void run() {

        }
    }

}
