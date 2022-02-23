package com.fuller.component.xrpc.provider;

import com.fuller.component.xrpc.MethodRegister;
import com.fuller.component.xrpc.ServerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.annotation.XRPC;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/23
 */
@Slf4j
@Component
@SuppressWarnings("rawtypes")
public class RpcServerBootstrap implements CommandLineRunner, ApplicationContextAware, DisposableBean {

    private ApplicationContext applicationContext;

    private Server grpcServer;

    private ServerRegister serverRegister;

    private MethodRegister methodRegister;

    @Override
    public void run(String... args) throws Exception {
        log.info("[gRPC][server]正在启动服务端程序...");
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(8001);
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(XRPC.class);
        beans.forEach((name, bean) -> this.bindService(bean, serverBuilder));
        this.grpcServer = serverBuilder.build();
        this.grpcServer.start();
        log.info("[gRPC][server]服务端启动完成.监听端口号:8001");
    }

    private void bindService(Object bean, ServerBuilder<?> serverBuilder) {
        Class<?> target = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
        for (Class<?> anInterface : target.getInterfaces()) {
            if (anInterface.isAnnotationPresent(XRPC.class)) {
                int count = 0;
                ServiceDefinition definition = serverRegister.parseServiceDefinition(anInterface);
                ServiceDescriptor serviceDescriptor = serverRegister.parseServiceDescriptor(anInterface);
                ServerServiceDefinition.Builder builder = ServerServiceDefinition.builder(serviceDescriptor);
                for (Method method : anInterface.getDeclaredMethods()) {
                    MethodDescriptor md = methodRegister.parseMethodDescriptor(definition, method);
                    ServerCallHandler handler = methodRegister.parseServerCallHandler(bean,method);
                    builder.addMethod(md, handler);
                    count++;
                }
                serverBuilder.addService(builder.build());
                log.info("[gRPC][server]识别到RPC服务:{},已暴露{}个RPC方法.", anInterface.getName(), count);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        if (!grpcServer.isShutdown()) {
            grpcServer.shutdown();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.methodRegister = applicationContext.getBean(MethodRegister.class);
        this.serverRegister = applicationContext.getBean(ServerRegister.class);
    }


}
