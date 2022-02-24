package com.fuller.component.xrpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuller.component.xrpc.consumer.*;
import com.fuller.component.xrpc.marshaller.DefaultMarshallerRegister;
import com.fuller.component.xrpc.marshaller.JacksonMarshallerFactory;
import com.fuller.component.xrpc.marshaller.MarshallerFactory;
import com.fuller.component.xrpc.marshaller.MarshallerRegister;
import io.netty.util.NettyRuntime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Allen Huang on 2022/2/11
 */
@Configuration
@ComponentScan
public class XRPCAutoConfiguration {

    @Bean
    @ConditionalOnClass(NettyRuntime.class)
    @ConditionalOnMissingBean(ConsumerChannelFactory.class)
    public ConsumerChannelFactory defaultConsumerChannelFactory() {
        return new NettyConsumerChannelFactory();
    }

    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    public MarshallerFactory jacksonMarshallerFactory(ObjectMapper objectMapper) {
        return new JacksonMarshallerFactory(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(MarshallerRegister.class)
    public MarshallerRegister marshallerRegister(MarshallerFactory marshallerFactory,
                                                 List<MarshallerCustomizer> customizers) {
        DefaultMarshallerRegister register = new DefaultMarshallerRegister(marshallerFactory);
        for (MarshallerCustomizer customizer : customizers) {
            customizer.customize(register);
        }
        return register;
    }

    @Bean
    @ConditionalOnMissingBean(ConsumerContext.class)
    public ConsumerContext defaultConsumerProxyRegister(MethodRegister register) {
        return new DefaultConsumerContext(register);
    }

    @Bean
    public ConsumerInject defaultXRPCReferenceInject(ConsumerContext context,
                                                     ServerRegister serverRegister) {
        return new ConsumerInject(context, serverRegister);
    }

}
