package com.github.zhaoli.rpc.autoconfig.beanpostprocessor;

import com.github.zhaoli.rpc.common.enumeration.ErrorEnum;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.config.ServiceConfig;
import com.github.zhaoli.rpc.autoconfig.annotation.RPCService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

/**
 * @author zhaoli
 * @date 2018/7/15
 */
@Slf4j
public class RPCProviderBeanPostProcessor extends AbstractRPCBeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (!beanClass.isAnnotationPresent(RPCService.class)) {
            return bean;
        }

        RPCService rpcService = beanClass.getAnnotation(RPCService.class);
        Class<?> interfaceClass = rpcService.interfaceClass();
        if (interfaceClass == void.class) {
            Class<?>[] interfaces = beanClass.getInterfaces();
            if (interfaces.length >= 1) {
                interfaceClass = interfaces[0];
            } else {
                throw new RPCException(ErrorEnum.SERVICE_DID_NOT_IMPLEMENT_ANY_INTERFACE,"该服务 {} 未实现任何服务接口", beanClass);
            }
        }
        ServiceConfig<Object> config = ServiceConfig.builder()
                .interfaceName(interfaceClass.getName())
                .interfaceClass((Class<Object>) interfaceClass)
                .isCallback(rpcService.callback())
                .callbackMethod(rpcService.callbackMethod())
                .callbackParamIndex(rpcService.callbackParamIndex())
                .ref(bean).build();
        initConfig(config);
        config.export();
        log.info("暴露服务:{}", interfaceClass);
        return bean;
    }
}
