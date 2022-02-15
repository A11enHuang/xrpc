package com.fuller.component.xrpc.provider;

import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.annotation.XRPC;
import com.fuller.component.xrpc.register.MethodRegister;
import com.fuller.component.xrpc.register.ServiceRegister;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Allen Huang on 2022/2/11
 */
@Component
public class ServiceExport implements CommandLineRunner, ApplicationContextAware, EnvironmentAware, DisposableBean {

    private ApplicationContext applicationContext;

    private MethodRegister methodRegister;

    private ServiceRegister serviceRegister;

    private Environment environment;

    private Server grpcServer;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.methodRegister = applicationContext.getBean(MethodRegister.class);
        this.serviceRegister = applicationContext.getBean(ServiceRegister.class);
    }

    @Override
    public void run(String... args) throws Exception {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(8001);
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(XRPC.class);
        beans.forEach((name, bean) -> this.bindService(bean, serverBuilder));
        this.grpcServer = serverBuilder.build();
        this.grpcServer.start();
    }

    private void bindService(Object bean, ServerBuilder<?> serverBuilder) {
        Class<?> target = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
        for (Class<?> anInterface : target.getInterfaces()) {
            if (anInterface.isAnnotationPresent(XRPC.class)) {
                System.out.println(anInterface.getName());
                ServiceDefinition serviceDefinition = ServiceDefinition.build(anInterface, environment);
                System.out.println(serviceDefinition);
//                ServiceDescriptor serviceDescriptor = serviceRegister.getServiceDescriptor(serviceDefinition);
//                ServerServiceDefinition.Builder builder = ServerServiceDefinition.builder(serviceDescriptor);
                //TODO: 添加对方法的定义
//                serverBuilder.addService(builder.build());
            }
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void destroy() throws Exception {
        if (!grpcServer.isShutdown()) {
            grpcServer.shutdown();
        }
    }
}
