package com.fuller.component.xrpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuller.component.xrpc.channel.ManagedChannelFactory;
import com.fuller.component.xrpc.channel.NettyManagedChannelFactory;
import com.fuller.component.xrpc.checker.DefaultServiceChecker;
import com.fuller.component.xrpc.checker.ServiceChecker;
import com.fuller.component.xrpc.register.JacksonMarshallerFactory;
import com.fuller.component.xrpc.register.MarshallerFactory;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Allen Huang on 2022/2/11
 */
@Configuration
@ComponentScan
public class XRPCAutoConfiguration {

    @Bean
    @ConditionalOnClass(NettyChannelBuilder.class)
    public ManagedChannelFactory nettyManagedChannelFactory() {
        return new NettyManagedChannelFactory();
    }

    @Bean
    @ConditionalOnMissingBean(ServiceChecker.class)
    public ServiceChecker defaultServiceChecker() {
        return new DefaultServiceChecker();
    }

    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    public MarshallerFactory jacksonMarshallerFactory(ObjectMapper objectMapper){
        return new JacksonMarshallerFactory(objectMapper);
    }

}
