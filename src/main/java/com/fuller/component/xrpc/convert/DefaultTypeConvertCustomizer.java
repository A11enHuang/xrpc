package com.fuller.component.xrpc.convert;

import com.fuller.component.xrpc.TypeConvertCustomizer;
import com.fuller.component.xrpc.TypeConvertRegister;
import org.springframework.stereotype.Component;

/**
 * @author Allen Huang on 2022/2/24
 */
@Component
public class DefaultTypeConvertCustomizer implements TypeConvertCustomizer {

    @Override
    public void customize(TypeConvertRegister register) {
        if (register.existsConvert(void.class)) {
            register.registerConvert(void.class, new VoidTypeConvert());
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
