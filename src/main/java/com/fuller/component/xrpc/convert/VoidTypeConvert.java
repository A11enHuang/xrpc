package com.fuller.component.xrpc.convert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * @author Allen Huang on 2022/2/23
 */
public class VoidTypeConvert implements TypeConvert<Void> {

    private static Void INSTANCE;

    static {
        try {
            Constructor<?> constructor = Void.class.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            INSTANCE = (Void) constructor.newInstance();
        } catch (Exception e) {
            //忽略异常
        }
    }

    @Override
    public Void convertFrom(Object obj) {
        return null;
    }

    @Override
    public Object convertTo(Void request) {
        return INSTANCE;
    }

    @Override
    public Type getDataType() {
        return void.class;
    }

    @Override
    public boolean isStream() {
        return false;
    }

}
