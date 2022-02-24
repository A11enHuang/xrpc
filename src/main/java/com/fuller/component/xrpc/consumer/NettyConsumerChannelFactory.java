package com.fuller.component.xrpc.consumer;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Allen Huang on 2022/2/23
 */
@Slf4j
public class NettyConsumerChannelFactory implements ConsumerChannelFactory, DisposableBean {
    private final Map<String, ManagedChannel> channelMap = new ConcurrentHashMap<>();

    @Override
    public Channel getChannel(String host, int port) {
        String key = host + ":" + port;
        return channelMap.computeIfAbsent(key, k -> {
            log.info("[gRPC][client]新建Channel成功.hostname:{},port:{}", host, port);
            return NettyChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .build();
        });
    }

    @Override
    public void destroy() throws Exception {
        if (this.channelMap.size() > 0) {
            for (Map.Entry<String, ManagedChannel> entry : channelMap.entrySet()) {
                ManagedChannel channel = entry.getValue();
                try {
                    if (!channel.isShutdown()) {
                        channel.shutdown();
                    }
                } catch (Exception e) {
                    log.error("gRPC通道关闭失败.", e);
                }
            }
        }
    }

}
