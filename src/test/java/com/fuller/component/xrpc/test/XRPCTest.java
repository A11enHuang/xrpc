package com.fuller.component.xrpc.test;

import com.fuller.component.xrpc.MethodRegister;
import com.fuller.component.xrpc.ServerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.annotation.XRPC;
import com.fuller.component.xrpc.consumer.ConsumerCaller;
import com.fuller.component.xrpc.consumer.ConsumerProxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/23
 */
@SpringBootTest(classes = XRPCTest.Config.class)
public class XRPCTest {

    @Autowired
    private MethodRegister methodRegister;
    @Autowired
    private ServerRegister serverRegister;

    @Test
    public void callTest() {
        ServiceDefinition definition = serverRegister.parseServiceDefinition(DemoService.class);
        Map<Method, ConsumerCaller> callerMap = new HashMap<>();
        for (Method method : DemoService.class.getDeclaredMethods()) {
            ConsumerCaller caller = methodRegister.parseConsumerCaller(definition, method);
            callerMap.put(method, caller);
        }
        DemoService demoService = ConsumerProxy.create(DemoService.class, callerMap);

        //有参数，有返回值
        System.out.println(demoService.normal("Java"));
        //有参数无返回值
        demoService.onlyParameter("Java");
        //无参数有返回值
        System.out.println(demoService.onlyReturn());
        //无参数无返回值
        demoService.allVoid();
    }


    @Configuration
    @EnableAutoConfiguration
    public static class Config {

        @Bean
        public DemoService demoService() {
            return new DemoServiceImpl();
        }

    }

    @XRPC(hostname = "localhost")
    public interface DemoService {
        //有参数有返回值
        String normal(String name);

        //有参数无返回值
        void onlyParameter(String name);

        //无参数有返回值
        String onlyReturn();

        //无参数无返回值
        void allVoid();
    }

    @Slf4j
    public static class DemoServiceImpl implements DemoService {

        @Override
        public String normal(String name) {
            log.info("[server]{}调用了正常的方法", name);
            return "normal";
        }

        @Override
        public void onlyParameter(String name) {
            log.info("[server]{}调用了只有参数的方法", name);
        }

        @Override
        public String onlyReturn() {
            log.info("[server]调用了只有返回值的方法");
            return "onlyReturn";
        }

        @Override
        public void allVoid() {
            log.info("[server]调用了全部都为空的方法");
        }
    }

}
