package com.fuller.component.xrpc.test;

import com.fuller.component.xrpc.MethodRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.annotation.XRPC;
import com.fuller.component.xrpc.consumer.ClientCaller;
import com.fuller.component.xrpc.consumer.ConsumerProxy;
import com.fuller.component.xrpc.util.ClassLoaderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/11
 */
@SpringBootTest(classes = ConsumerRegisterTest.Config.class)
public class ConsumerRegisterTest {

    @Autowired
    private DemoService demoService;

    @Autowired
    private MethodRegister methodRegister;

    @Test
    public void registerTest() {
        System.out.println(demoService);
    }

    @Test
    public void clientTest() {
        Class<DemoService> target = DemoService.class;
        ServiceDefinition sd = new ServiceDefinition();
        sd.setType(target);
        sd.setServiceName(target.getSimpleName());
        sd.setHostname("127.0.0.1");
        sd.setPort(8001);

        Map<Method,ClientCaller> map = new HashMap<>();
        for (Method method : target.getDeclaredMethods()) {
            map.put(method,methodRegister.getClientCaller(sd, method));
        }
        ClassLoader classLoader = ClassLoaderUtil.getContextClassLoader();
        DemoService service = (DemoService)Proxy.newProxyInstance(classLoader, new Class<?>[]{target}, new ConsumerProxy(map));
//        String response = service.hello("allen");
//        System.out.println(response);
        service.run();
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {

        @Bean
        public DemoService demoService() {
            return new DemoServiceImpl();
        }

    }

    @XRPC(hostname = "service-demo.microservice")
    public interface DemoService {

        void run();

        String hello(String name);

    }

    public static class DemoServiceImpl implements DemoService {

        @Override
        public void run() {
            System.out.println("run被调用了");
        }

        @Override
        public String hello(String name) {
            System.out.println(name + " say hello!");
            return "hello world";
        }
    }

}
