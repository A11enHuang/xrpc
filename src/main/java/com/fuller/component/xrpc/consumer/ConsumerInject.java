package com.fuller.component.xrpc.consumer;

import com.fuller.component.xrpc.ServerRegister;
import com.fuller.component.xrpc.ServiceDefinition;
import com.fuller.component.xrpc.annotation.XRPC;
import com.fuller.component.xrpc.annotation.XRPCReference;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * @author Allen Huang on 2022/2/23
 */
@RequiredArgsConstructor
public class ConsumerInject implements BeanPostProcessor {

    private final ConsumerContext consumerContext;
    private final ServerRegister serverRegister;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> target = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
        for (Class<?> parent = target; parent != null; parent = parent.getSuperclass()) {
            Field[] fields = parent.getDeclaredFields();
            for (Field field : fields) {
                XRPCReference annotation = field.getAnnotation(XRPCReference.class);
                if (annotation != null) {
                    injectReference(target, bean, field, annotation);
                }
            }
        }
        return bean;
    }

    protected void injectReference(Class<?> type, Object bean, Field field, XRPCReference annotation) {
        Class<?> reference = field.getType();
        if (!reference.isInterface()) {
            throw new BeanCreationException("被@XRPCReference标记的属性必须是一个接口.错误的字段定义:" + type.getName() + "#" + field.getName());
        }
        XRPC xrpc = reference.getAnnotation(XRPC.class);
        if (xrpc == null) {
            throw new BeanCreationException("被@XRPCReference标记的属性接口必须包含@XRPC注解.错误的字段定义:" + type.getName() + "#" + field.getName());
        }
        ServiceDefinition definition = serverRegister.parseServiceDefinition(reference);
        if (StringUtils.hasText(annotation.hostname())) {
            definition.setHostname(annotation.hostname());
        }
        if (annotation.port() > 0) {
            definition.setPort(annotation.port());
        }
        Object instance = consumerContext.getProxy(definition);
        try {
            boolean canAccess = field.canAccess(bean);
            field.setAccessible(true);
            field.set(bean, instance);
            field.setAccessible(canAccess);
        } catch (Exception e) {
            throw new BeanCreationException("注入属性失败", e);
        }
    }

}
