package com.fuller.component.xrpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuller.component.xrpc.consumer.ConsumerChannelFactory;
import com.fuller.component.xrpc.consumer.NettyConsumerChannelFactory;
import com.fuller.component.xrpc.consumer.ConsumerContext;
import com.fuller.component.xrpc.consumer.ConsumerInject;
import com.fuller.component.xrpc.consumer.DefaultConsumerContext;
import com.fuller.component.xrpc.marshaller.JacksonMarshallerFactory;
import com.fuller.component.xrpc.marshaller.MarshallerFactory;
import io.grpc.netty.NettyChannelBuilder;
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
    public ConsumerChannelFactory nettyManagedChannelFactory() {
        return new NettyConsumerChannelFactory();
    }

    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    public MarshallerFactory jacksonMarshallerFactory(ObjectMapper objectMapper) {
        return new JacksonMarshallerFactory(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(MarshallerRegister.class)
    public MarshallerRegister marshallerRegister(MarshallerFactory marshallerFactory) {
        return new DefaultMarshallerRegister(marshallerFactory);
    }

    @Bean
    @ConditionalOnMissingBean(ConsumerContext.class)
    public ConsumerContext defaultConsumerProxyRegister(MethodRegister register) {
        return new DefaultConsumerContext(register);
    }

    @Bean
    public ConsumerInject defaultXRPCReferenceInject(ConsumerContext register) {
        return new ConsumerInject(register);
    }

}
