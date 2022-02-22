package com.fuller.component.xrpc.test;

import com.fuller.component.xrpc.annotation.XRPC;
import com.fuller.component.xrpc.annotation.XRPCReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Allen Huang on 2022/2/16
 */
@SpringBootTest(classes = ReferenceTest.Config.class)
public class ReferenceTest {

    @Autowired
    private DemoService demoService;

    @Test
    public void testIn(){
        demoService.printService();
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {

        @Bean
        public DemoService referenceService(){
            return new DemoService();
        }

        @Bean
        public HelloService helloService(){
            return new HelloServiceImpl();
        }

    }

    @XRPC(hostname = "HelloService")
    public interface HelloService {
        String sayHello(String say);
    }

    public static class HelloServiceImpl implements HelloService{

        @Override
        public String sayHello(String say) {
            System.out.println(say + " say hello.");
            return "hello world";
        }
    }

    public static class DemoService {

        @XRPCReference(hostname = "localhost")
        private HelloService helloService;

        public void printService(){
            helloService.sayHello("allen");
        }

    }


}
