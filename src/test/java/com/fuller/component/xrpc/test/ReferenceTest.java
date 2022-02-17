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
    private ReferenceService referenceService;

    @Test
    public void testIn(){
        referenceService.printService();
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {

        @Bean
        public ReferenceService referenceService(){
            return new ReferenceService();
        }

    }

    @XRPC(appName = "HelloService")
    public interface HelloService {
        String sayHello();
    }

    public static class ReferenceService {

        @XRPCReference
        private HelloService helloService;

        public void printService(){
            System.out.println(helloService);
        }

    }


}
