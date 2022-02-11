package com.fuller.component.xrpc.test;

import com.fuller.component.xrpc.annotation.EnableXRPC;
import com.fuller.component.xrpc.annotation.XRPC;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Allen Huang on 2022/2/11
 */
@SpringBootTest(classes = ConsumerRegisterTest.Config.class,properties = {
        "spring.service.demo.name=service-demo.microservice"
})
public class ConsumerRegisterTest {

    @Autowired
    private DemoService demoService;

    @Test
    public void registerTest() {
        System.out.println(demoService);
    }

    @EnableAutoConfiguration
    @EnableXRPC(clients = DemoService.class)
    public static class Config {

    }

    @XRPC(appName = "${spring.service.demo.name}", port = 8888)
    public interface DemoService {

        String say();

    }

}
