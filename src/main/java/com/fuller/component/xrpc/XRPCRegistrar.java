package com.fuller.component.xrpc;

import com.fuller.component.xrpc.annotation.EnableXRPC;
import com.fuller.component.xrpc.annotation.XRPC;
import com.fuller.component.xrpc.consumer.InvokerFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 扫描@XRPC注解并生成代理对象，注册到Spring容器中
 *
 * @author Allen Huang on 2022/2/11
 */
@Slf4j
@Import(XRPCAutoConfiguration.class)
public class XRPCRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableXRPC.class.getName());
        final Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get("clients");

        if (clients == null || clients.length == 0) {
            ClassPathScanningCandidateComponentProvider scanner = getScanner();
            scanner.setResourceLoader(this.resourceLoader);
            scanner.addIncludeFilter(new AnnotationTypeFilter(XRPC.class));
            Set<String> basePackages = getBasePackages(metadata);
            for (String basePackage : basePackages) {
                candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
            }
        } else {
            for (Class<?> clazz : clients) {
                candidateComponents.add(new AnnotatedGenericBeanDefinition(clazz));
            }
        }
        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                Assert.isTrue(annotationMetadata.isInterface(), "@XRPC注解只能定义在接口上");

                Map<String, Object> attributes = annotationMetadata
                        .getAnnotationAttributes(XRPC.class.getCanonicalName());

                registerXRPCClient(registry, annotationMetadata, attributes);
            }
        }
        log.info("[XRPC]已经完成{}个消费者实例注册.", candidateComponents.size());
    }

    private void registerXRPCClient(BeanDefinitionRegistry registry,
                                    AnnotationMetadata annotationMetadata,
                                    Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(InvokerFactoryBean.class);

        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setAppName(getAppName(attributes));
        serviceDefinition.setPort(getPort(attributes));
        serviceDefinition.setVersion(getVersion(attributes));
        serviceDefinition.setServiceName(getServiceName(attributes));

        definition.addPropertyValue("type", className);
        definition.addPropertyValue("serviceDefinition", serviceDefinition);

        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);

        boolean primary = (Boolean) attributes.get("primary");
        beanDefinition.setPrimary(primary);

        String alias = className.substring(className.lastIndexOf(".") + 1) + "XRPClient";
        String qualifier = getQualifier(attributes);
        if (StringUtils.hasText(qualifier)) {
            alias = qualifier;
        }
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[]{alias});

        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        if (log.isDebugEnabled()) {
            log.debug("成功注册XRPC客户端:{}", className);
        }
    }

    private String getQualifier(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String qualifier = (String) client.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    private int getPort(Map<String, Object> attributes) {
        return (int) attributes.get("port");
    }

    private String getVersion(Map<String, Object> attributes) {
        return (String) attributes.get("version");
    }

    private String getAppName(Map<String, Object> attributes) {
        String name = (String) attributes.get("appName");
        return this.environment.resolvePlaceholders(name);
    }

    private String getServiceName(Map<String, Object> attributes) {
        return Optional.ofNullable((String) attributes.get("serviceName"))
                .map(environment::resolvePlaceholders)
                .filter(StringUtils::hasText)
                .orElseGet(() -> getAppName(attributes));
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableXRPC.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

}
