package com.fuller.component.xrpc;

import com.fuller.component.xrpc.consumer.ConsumerCaller;
import com.fuller.component.xrpc.consumer.ConsumerChannelFactory;
import com.fuller.component.xrpc.consumer.UnaryConsumerCaller;
import com.fuller.component.xrpc.convert.DefaultTypeConvert;
import com.fuller.component.xrpc.convert.TypeConvert;
import com.fuller.component.xrpc.convert.VoidTypeConvert;
import com.fuller.component.xrpc.marshaller.MarshallerRegister;
import com.fuller.component.xrpc.parser.MethodParameterParser;
import com.fuller.component.xrpc.provider.ServerAsyncUnaryCallMethod;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.ServiceDescriptor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Allen Huang on 2022/2/23
 */
@Component
@SuppressWarnings("rawtypes")
public class Configuration implements TypeConvertRegister, ServerRegister, MethodRegister, EnvironmentAware {

    private final Map<Method, Type> methodParameterType = new HashMap<>();
    private final Map<Type, TypeConvert> typeConvertMap = new HashMap<>();
    private final Map<Method, ConsumerCaller> consumerCallerMap = new HashMap<>();
    private final Map<Method, MethodDescriptor> methodDescriptors = new HashMap<>();
    private final Map<Class<?>, ServiceDescriptor> serviceDescriptors = new HashMap<>();

    private final MarshallerRegister marshallerRegister;
    private final ConsumerChannelFactory channelFactory;
    private final List<MethodParameterParser> parameterParsers;

    private Environment environment;

    public Configuration(MarshallerRegister marshallerRegister,
                         ConsumerChannelFactory channelFactory,
                         List<MethodParameterParser> parameterParsers) {
        this.marshallerRegister = marshallerRegister;
        this.parameterParsers = parameterParsers;
        this.channelFactory = channelFactory;
        typeConvertMap.put(Void.TYPE, new VoidTypeConvert());
    }

    @Override
    public ServiceDescriptor parseServiceDescriptor(Class<?> type) {
        if (!type.isInterface()) {
            throw new BeanCreationException("RPC定义类必须是一个接口.");
        }
        ServiceDescriptor descriptor = serviceDescriptors.get(type);
        if (descriptor == null) {
            synchronized (this.serviceDescriptors) {
                descriptor = serviceDescriptors.get(type);
                if (descriptor == null) {
                    ServiceDefinition definition = parseServiceDefinition(type);
                    ServiceDescriptor.Builder builder = ServiceDescriptor.newBuilder(definition.getServiceName());
                    for (Class<?> parent = type; parent != null; parent = parent.getSuperclass()) {
                        for (Method method : parent.getDeclaredMethods()) {
                            builder.addMethod(parseMethodDescriptor(definition, method));
                        }
                    }
                    descriptor = builder.build();
                    serviceDescriptors.put(type, descriptor);
                }
            }
        }
        return descriptor;
    }

    @Override
    public MethodDescriptor parseMethodDescriptor(ServiceDefinition definition, Method method) {
        MethodDescriptor methodDescriptor = methodDescriptors.get(method);
        if (methodDescriptor == null) {
            synchronized (methodDescriptors) {
                methodDescriptor = methodDescriptors.get(method);
                if (methodDescriptor == null) {
                    Type parameterType = parseParameterType(method);
                    Type returnType = method.getGenericReturnType();
                    TypeConvert requestConvert = getConvert(parameterType);
                    TypeConvert responseConvert = getConvert(returnType);
                    methodDescriptor = MethodDescriptor.newBuilder()
                            .setType(matchMethodType(requestConvert, responseConvert))
                            .setFullMethodName(definition.getServiceName() + "/" + method.getName())
                            .setSampledToLocalTracing(true)
                            .setRequestMarshaller(marshallerRegister.getMarshaller(requestConvert.getDataType()))
                            .setResponseMarshaller(marshallerRegister.getMarshaller(responseConvert.getDataType()))
                            .build();
                    methodDescriptors.put(method, methodDescriptor);
                }
            }
        }
        return methodDescriptor;
    }

    @Override
    public ServerCallHandler parseServerCallHandler(Object bean, Method method) {
        Type parameterType = parseParameterType(method);
        Type returnType = method.getGenericReturnType();
        TypeConvert requestConvert = getConvert(parameterType);
        TypeConvert responseConvert = getConvert(returnType);
        MethodDescriptor.MethodType methodType = matchMethodType(requestConvert, responseConvert);
        if (methodType == MethodDescriptor.MethodType.UNARY) {
            return new ServerAsyncUnaryCallMethod(bean, method, requestConvert, responseConvert).getServerCallHandler();
        }
        return null;
    }

    @Override
    public ConsumerCaller parseConsumerCaller(ServiceDefinition definition, Method method) {
        ConsumerCaller caller = consumerCallerMap.get(method);
        if (caller == null) {
            synchronized (consumerCallerMap) {
                caller = consumerCallerMap.get(method);
                if (caller == null) {
                    caller = buildConsumerCaller(definition, method);
                }
            }
        }
        if (caller == null) {
            throw new BeanCreationException("无法解析RPC方法." + method.getDeclaringClass().getName() + "#" + method.getName());
        }
        return caller;
    }

    protected ConsumerCaller buildConsumerCaller(ServiceDefinition definition, Method method) {
        ConsumerCaller caller = null;
        for (MethodParameterParser parser : parameterParsers) {
            if (parser.isSupport(method)) {
                Type parameterType = parser.parse(method);
                Type returnType = method.getGenericReturnType();
                TypeConvert requestConvert = getConvert(parameterType);
                TypeConvert responseConvert = getConvert(returnType);
                MethodDescriptor.MethodType methodType = matchMethodType(requestConvert, responseConvert);
                if (methodType == MethodDescriptor.MethodType.UNARY) {
                    MethodDescriptor md = parseMethodDescriptor(definition, method);
                    Channel channel = channelFactory.getChannel(definition.getHostname(), definition.getPort());
                    caller = new UnaryConsumerCaller(method, channel, parser, md, requestConvert, responseConvert);
                }
                break;
            }
        }

        return caller;
    }

    protected Type parseParameterType(Method method) {
        Type pt = methodParameterType.get(method);
        if (pt == null) {
            synchronized (methodParameterType) {
                for (MethodParameterParser parser : parameterParsers) {
                    if (parser.isSupport(method)) {
                        pt = parser.parse(method);
                        methodParameterType.put(method, pt);
                        break;
                    }
                }
            }
        }
        return pt;
    }


    @Override
    public TypeConvert getConvert(Type type) {
        TypeConvert convert = typeConvertMap.get(type);
        if (convert == null) {
            synchronized (typeConvertMap) {
                convert = typeConvertMap.get(type);
                if (convert == null) {
                    convert = new DefaultTypeConvert(type);
                    typeConvertMap.put(type, convert);
                }
            }
        }
        return convert;
    }

    protected MethodDescriptor.MethodType matchMethodType(TypeConvert requestConvert, TypeConvert responseConvert) {
        //根据请求参数和响应参数判断请求的类型：
        //当请求为流模式时，如果响应为流模式，则方法是BIDI_STREAMING模式，否则方法是CLIENT_STREAMING模式
        //当请求为非流模式时，如果响应为流模式，则方法是SERVER_STREAMING模式，否则方法是UNARY模式
        if (requestConvert.isStream()) {
            return responseConvert.isStream() ? MethodDescriptor.MethodType.BIDI_STREAMING : MethodDescriptor.MethodType.CLIENT_STREAMING;
        } else {
            return responseConvert.isStream() ? MethodDescriptor.MethodType.SERVER_STREAMING : MethodDescriptor.MethodType.UNARY;
        }
    }

    @Override
    public ServiceDefinition parseServiceDefinition(Class<?> type) {
        return ServiceDefinition.build(type, environment);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


}
