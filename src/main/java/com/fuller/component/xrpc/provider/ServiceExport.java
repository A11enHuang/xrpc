package com.fuller.component.xrpc.provider;

import com.fuller.component.xrpc.MethodRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.ServiceRegister;
import com.fuller.component.xrpc.annotation.XRPC;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
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

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/11
 */
@Slf4j
@Component
@SuppressWarnings("rawtypes")
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
        log.info("[gRPC]Server启动完成.监听端口号:8001");
    }

    private void bindService(Object bean, ServerBuilder<?> serverBuilder) {
        Class<?> target = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
        for (Class<?> anInterface : target.getInterfaces()) {
            if (anInterface.isAnnotationPresent(XRPC.class)) {
                ServiceDefinition serviceDefinition = ServiceDefinition.build(anInterface, environment);
                ServiceDescriptor serviceDescriptor = serviceRegister.getServiceDescriptor(serviceDefinition);
                ServerServiceDefinition.Builder builder = ServerServiceDefinition.builder(serviceDescriptor);
                for (Method method : anInterface.getDeclaredMethods()) {
                    MethodDescriptor md = methodRegister.getMethodDescriptor(serviceDefinition, method);
                    ServerCallHandler handler = methodRegister.getServerCallHandler(serviceDefinition, method, bean);
                    builder.addMethod(md, handler);
                }
                serverBuilder.addService(builder.build());
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
