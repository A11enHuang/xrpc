package com.fuller.component.xrpc.test;

import com.fuller.component.xrpc.annotation.XRPCReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Allen Huang on 2022/2/23
 */
@SpringBootTest(classes = ReferenceTest.Config.class)
public class ReferenceTest {

    @Autowired
    private ReferenceService referenceService;

    @Test
    public void injectTest() {
        referenceService.test();
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {

        @Bean
        public ReferenceService referenceService() {
            return new ReferenceService();
        }

    }

    public static class ReferenceService {

        @XRPCReference
        private XRPCTest.DemoService demoService;

        public void test() {
            System.out.println(demoService.normal("Java"));
            demoService.onlyParameter("Java");
            System.out.println(demoService.onlyReturn());
            demoService.allVoid();
        }

    }

}
