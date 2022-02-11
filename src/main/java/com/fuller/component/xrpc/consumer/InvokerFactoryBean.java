package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.util.ClassLoaderUtil;
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

    private String version;

    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        ClassLoader classLoader = ClassLoaderUtil.getContextClassLoader();
        return Proxy.newProxyInstance(classLoader, new Class<?>[]{type}, new Invoker(Map.of()));
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
