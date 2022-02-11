package com.fuller.component.xrpc;

import com.fuller.component.xrpc.channel.ChannelRegister;
import com.fuller.component.xrpc.channel.NettyChannelRegister;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
    public ChannelRegister nettyManagedChannel() {
        return new NettyChannelRegister();
    }

}
