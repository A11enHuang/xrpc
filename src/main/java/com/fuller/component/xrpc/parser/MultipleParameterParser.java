package com.fuller.component.xrpc.parser;

import com.fuller.component.xrpc.util.ParameterizedTypeImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多参数解析器
 *
 * @author Allen Huang on 2022/2/24
 */
@Component
public class MultipleParameterParser implements MethodParameterParser {

    private static final Map<Integer, Handler> handlerMap = new HashMap<>(8);

    static {
        handlerMap.put(2, new Multiple2Wrapper.WrapperHandler());
        handlerMap.put(3, new Multiple3Wrapper.WrapperHandler());
        handlerMap.put(4, new Multiple4Wrapper.WrapperHandler());
        handlerMap.put(5, new Multiple5Wrapper.WrapperHandler());
    }

    @Override
    public Type parse(Method method) {
        Handler handler = handlerMap.get(method.getParameterCount());
        return new ParameterizedTypeImpl(method.getGenericParameterTypes(), null, handler.getRawType());
    }

    @Override
    public Object toRpcValue(Method method, Object[] args) {
        Handler handler = handlerMap.get(method.getParameterCount());
        return handler.handleOut(method, args);
    }

    @Override
    public List<Object> toMethodValue(Method method, Object rpcValue) {
        Handler handler = handlerMap.get(method.getParameterCount());
        return handler.handleIn(method, rpcValue);
    }

    @Override
    public boolean isSupport(Method method) {
        int count = method.getParameterCount();
        if (count > 5) {
            throw new BeanCreationException("方法的行参不能超过五个.错误的方法:" + method.getDeclaringClass().getName() + "#" + method.getName());
        }
        return count > 1;
    }

    private interface Handler {
        Object handleOut(Method method, Object[] args);

        List<Object> handleIn(Method method, Object object);

        Type getRawType();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 10;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Multiple2Wrapper<T1, T2> {
        private T1 value1;
        private T2 value2;


        public static class WrapperHandler implements Handler {

            @Override
            public Object handleOut(Method method, Object[] args) {
                return new Multiple2Wrapper<>(args[0], args[1]);
            }

            @Override
            public List<Object> handleIn(Method method, Object object) {
                if (object == null) {
                    return Collections.emptyList();
                }
                Multiple2Wrapper wrapper = ((Multiple2Wrapper) object);
                return List.of(wrapper.getValue1(), wrapper.getValue2());
            }

            @Override
            public Type getRawType() {
                return Multiple2Wrapper.class;
            }

        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Multiple3Wrapper<T1, T2, T3> {
        private T1 value1;
        private T2 value2;
        private T3 value3;

        public static class WrapperHandler implements Handler {

            @Override
            public Object handleOut(Method method, Object[] args) {
                return new Multiple3Wrapper<>(args[0], args[1], args[2]);
            }

            @Override
            public List<Object> handleIn(Method method, Object object) {
                if (object == null) {
                    return Collections.emptyList();
                }
                Multiple3Wrapper wrapper = ((Multiple3Wrapper) object);
                return List.of(wrapper.getValue1(), wrapper.getValue2(), wrapper.getValue3());
            }

            @Override
            public Type getRawType() {
                return Multiple3Wrapper.class;
            }

        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Multiple4Wrapper<T1, T2, T3, T4> {
        private T1 value1;
        private T2 value2;
        private T3 value3;
        private T4 value4;

        public static class WrapperHandler implements Handler {

            @Override
            public Object handleOut(Method method, Object[] args) {
                return new Multiple4Wrapper<>(args[0], args[1], args[2], args[3]);
            }

            @Override
            public List<Object> handleIn(Method method, Object object) {
                if (object == null) {
                    return Collections.emptyList();
                }
                Multiple4Wrapper wrapper = ((Multiple4Wrapper) object);
                return List.of(wrapper.getValue1(), wrapper.getValue2(), wrapper.getValue3(), wrapper.getValue4());
            }

            @Override
            public Type getRawType() {
                return Multiple4Wrapper.class;
            }

        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Multiple5Wrapper<T1, T2, T3, T4, T5> {
        private T1 value1;
        private T2 value2;
        private T3 value3;
        private T4 value4;
        private T5 value5;

        public static class WrapperHandler implements Handler {

            @Override
            public Object handleOut(Method method, Object[] args) {
                return new Multiple5Wrapper<>(args[0], args[1], args[2], args[3], args[4]);
            }

            @Override
            public List<Object> handleIn(Method method, Object object) {
                if (object == null) {
                    return Collections.emptyList();
                }
                Multiple5Wrapper wrapper = ((Multiple5Wrapper) object);
                return List.of(wrapper.getValue1(), wrapper.getValue2(), wrapper.getValue3(), wrapper.getValue4(), wrapper.getValue5());
            }

            @Override
            public Type getRawType() {
                return Multiple5Wrapper.class;
            }

        }
    }

}
