package com.fuller.component.xrpc;

import com.fuller.component.xrpc.marshaller.MarshallerRegister;
import org.springframework.core.Ordered;

/**
 * 序列化器自定义接口
 *
 * @author Allen Huang on 2022/2/24
 */
public interface MarshallerCustomizer extends Ordered {

    /**
     * 在此接口中完成自定义序列化器的注册
     *
     * @param register 序列化器注册器
     */
    void customize(MarshallerRegister register);

    @Override
    default int getOrder() {
        return 0;
    }

}
