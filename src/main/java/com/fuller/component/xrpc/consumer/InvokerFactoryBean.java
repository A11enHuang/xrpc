package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.channel.ManagedChannelFactory;
import com.fuller.component.xrpc.checker.ServiceChecker;
import com.fuller.component.xrpc.register.Configuration;
import com.fuller.component.xrpc.util.ClassLoaderUtil;
import io.grpc.ManagedChannel;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/11
 */
@Data
public class InvokerFactoryBean implements FactoryBean<Object>, ApplicationContextAware, InitializingBean {

    private Class<?> type;

    private ServiceDefinition serviceDefinition;

    private ApplicationContext applicationContext;

    private Configuration configuration;

    @Override
    public Object getObject() throws Exception {
        ClassLoader classLoader = ClassLoaderUtil.getContextClassLoader();
        //第一步: 检查服务的定义是否符合规范
        checkServiceDefinition();
        //第二步: 获取channel实例
        ManagedChannel channel = getManagedChannel();
        //TODO: 构建存根，每一个存根都是一个ClientCaller
        return Proxy.newProxyInstance(classLoader, new Class<?>[]{type}, new Invoker(Map.of()));
    }

    private void checkServiceDefinition() {
        ServiceChecker checker = this.applicationContext.getBean(ServiceChecker.class);
        checker.check(this.serviceDefinition);
    }

    private ManagedChannel getManagedChannel() {
        String hostname = this.serviceDefinition.getAppName();
        int port = this.serviceDefinition.getPort();
        ManagedChannelFactory managedChannelFactory = this.applicationContext.getBean(ManagedChannelFactory.class);
        return managedChannelFactory.getManagedChannel(hostname, port);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.serviceDefinition.setType(this.type);
        this.configuration = applicationContext.getBean(Configuration.class);
    }

}
