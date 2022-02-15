package com.fuller.component.xrpc.test;

import com.fuller.component.xrpc.annotation.XRPC;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Allen Huang on 2022/2/11
 */
@SpringBootTest(classes = ConsumerRegisterTest.Config.class)
public class ConsumerRegisterTest {

    @Autowired
    private DemoService demoService;

    @Test
    public void registerTest() {
        System.out.println(demoService);
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

        String say();

    }

    @XRPC(appName = "service-demo.microservice")
    public interface CarService {

        void run();

    }

    public static class DemoServiceImpl implements DemoService, CarService {

        @Override
        public String say() {
            return "hello world!";
        }

        @Override
        public void run() {

        }
    }

}
