package com.fuller.component.xrpc;

import com.fuller.component.xrpc.convert.DefaultTypeConvert;
import com.fuller.component.xrpc.convert.TypeConvert;
import com.fuller.component.xrpc.convert.VoidTypeConvert;
import com.fuller.component.xrpc.parser.MethodParameterParser;
import io.grpc.MethodDescriptor;
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
public class XRPConfiguration implements TypeConvertRegister, ServerRegister, EnvironmentAware {

    private final Map<Method, Type> methodParameterType = new HashMap<>();
    private final Map<Type, TypeConvert> typeConvertMap = new HashMap<>();
    private final Map<Method, MethodDescriptor> methodDescriptors = new HashMap<>();

    private final MarshallerRegister marshallerRegister;
    private final List<MethodParameterParser> parameterParsers;

    private Environment environment;

    public XRPConfiguration(MarshallerRegister marshallerRegister, List<MethodParameterParser> parameterParsers) {
        this.marshallerRegister = marshallerRegister;
        this.parameterParsers = parameterParsers;
        typeConvertMap.put(Void.TYPE, new VoidTypeConvert());
    }

    public MethodDescriptor getMethodDescriptor(ServiceDefinition definition, Method method) {
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

    protected Type parseParameterType(Method method) {
        Type pt = methodParameterType.get(method);
        if (pt == null) {
            synchronized (methodParameterType) {
                for (MethodParameterParser parser : parameterParsers) {
                    pt = parser.parse(method);
                    if (pt != null) {
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
