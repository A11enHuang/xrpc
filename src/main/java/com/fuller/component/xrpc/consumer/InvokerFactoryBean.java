package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.channel.ChannelRegister;
import com.fuller.component.xrpc.util.ClassLoaderUtil;
import io.grpc.ManagedChannel;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/11
 */
@Data
public class InvokerFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

    private Class<?> type;

    private int port;

    private String name;

    //版本号字段，是一个预留的字段，目前还没有什么用途
    private String version;

    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        ClassLoader classLoader = ClassLoaderUtil.getContextClassLoader();
        ManagedChannel channel = getManagedChannel();
        //TODO: 构建存根，每一个存根都是一个ClientCaller
        return Proxy.newProxyInstance(classLoader, new Class<?>[]{type}, new Invoker(Map.of()));
    }

    private ManagedChannel getManagedChannel() {
        ChannelRegister channelRegister = this.applicationContext.getBean(ChannelRegister.class);
        return channelRegister.getManagedChannel(this.name, this.port);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
