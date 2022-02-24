package com.fuller.component.xrpc.marshaller;

import com.fuller.component.xrpc.MarshallerCustomizer;
import org.springframework.stereotype.Component;

/**
 * @author Allen Huang on 2022/2/24
 */
@Component
public class DefaultMarshallerCustomizer implements MarshallerCustomizer {

    @Override
    public void customize(MarshallerRegister register) {
        if (!register.existsMarshaller(String.class)) {
            register.registerMarshaller(String.class, new StringMarshaller());
        }
        if (!register.existsMarshaller(void.class)) {
            register.registerMarshaller(void.class, new VoidMarshaller());
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
