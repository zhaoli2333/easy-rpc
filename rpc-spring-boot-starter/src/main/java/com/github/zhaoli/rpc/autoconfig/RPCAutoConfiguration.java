package com.github.zhaoli.rpc.autoconfig;


import com.github.zhaoli.rpc.autoconfig.beanpostprocessor.RPCConsumerBeanPostProcessor;
import com.github.zhaoli.rpc.autoconfig.beanpostprocessor.RPCProviderBeanPostProcessor;
import com.github.zhaoli.rpc.cluster.FaultToleranceHandler;
import com.github.zhaoli.rpc.common.ExtensionLoader;
import com.github.zhaoli.rpc.common.enumeration.*;
import com.github.zhaoli.rpc.common.exception.RPCException;
import com.github.zhaoli.rpc.config.*;
import com.github.zhaoli.rpc.executor.api.TaskExecutor;
import com.github.zhaoli.rpc.filter.Filter;
import com.github.zhaoli.rpc.filter.impl.ActiveLimitFilter;
import com.github.zhaoli.rpc.protocol.api.support.AbstractProtocol;
import com.github.zhaoli.rpc.proxy.api.RPCProxyFactory;
import com.github.zhaoli.rpc.cluster.loadbalance.LeastActiveLoadBalancer;
import com.github.zhaoli.rpc.cluster.support.AbstractLoadBalancer;
import com.github.zhaoli.rpc.registry.api.ServiceRegistry;
import com.github.zhaoli.rpc.registry.api.support.AbstractServiceRegistry;
import com.github.zhaoli.rpc.registry.zookeeper.ZkServiceRegistry;
import com.github.zhaoli.rpc.serialize.api.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author zhaoli
 * @date 2018/3/10
 */
@EnableConfigurationProperties(RPCProperties.class)
@Configuration
@Slf4j
public class RPCAutoConfiguration implements InitializingBean {
    @Autowired
    private RPCProperties properties;
    private ExtensionLoader extensionLoader;
    
    @Bean(initMethod = "init", destroyMethod = "close")
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = properties.getRegistry();
        if (registryConfig == null
                || StringUtils.isEmpty(registryConfig.getAddress())
                || StringUtils.isEmpty(registryConfig.getType())) {
            throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "必须配置注册中心address和type");
        }
        //TODO injvm协议不需要连接ZK
        if (ProtocolType.INJVM != ProtocolType.valueOf(properties.getProtocol().getType().toUpperCase())) {
//            ZkServiceRegistry serviceRegistry = new ZkServiceRegistry(registryConfig);
            AbstractServiceRegistry serviceRegistry = extensionLoader.load(AbstractServiceRegistry.class, RegistryType.class, registryConfig.getType());
            serviceRegistry.setRegistryConfig(registryConfig);
            registryConfig.setRegistryInstance(serviceRegistry);
        }
        log.info("{}", registryConfig);
        return registryConfig;
    }
    
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig application = properties.getApplication();
        if (application == null) {
            throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "必须配置applicationConfig");
        }
        application.setProxyFactoryInstance(extensionLoader.load(RPCProxyFactory.class, ProxyFactoryType.class, application.getProxy()));
        application.setSerializerInstance(extensionLoader.load(Serializer.class, SerializerType.class, application.getSerialize()));
        log.info("{}", application);
        return application;
    }


    @Bean(destroyMethod = "close")
    public ProtocolConfig protocolConfig(ApplicationConfig applicationConfig, RegistryConfig registryConfig, ClusterConfig clusterConfig) {
        ProtocolConfig protocolConfig = properties.getProtocol();
        if (protocolConfig == null) {
            throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "必须配置protocolConfig");
        }
        AbstractProtocol protocol = extensionLoader.load(AbstractProtocol.class, ProtocolType.class, protocolConfig.getType());
        protocol.init(GlobalConfig.builder()
                .applicationConfig(applicationConfig)
                .protocolConfig(protocolConfig)
                .clusterConfig(clusterConfig)
                .registryConfig(registryConfig)
                .build()
        );
        protocolConfig.setProtocolInstance(protocol);
        
        ((AbstractLoadBalancer) clusterConfig.getLoadBalanceInstance()).updateGlobalConfig(GlobalConfig.builder().protocolConfig(protocolConfig).build());
        Executors executors = protocolConfig.getExecutor();
        if (executors != null) {
            ExecutorConfig serverExecutor = executors.getServer();
            if (serverExecutor != null) {
                TaskExecutor executor = extensionLoader.load(TaskExecutor.class, ExecutorType.class, serverExecutor.getType());
                executor.init(serverExecutor.getThreads());
                serverExecutor.setExecutorInstance(executor);
            }
            ExecutorConfig clientExecutor = executors.getClient();
            if (clientExecutor != null) {
                TaskExecutor executor = extensionLoader.load(TaskExecutor.class, ExecutorType.class, clientExecutor.getType());
                executor.init(clientExecutor.getThreads());
                clientExecutor.setExecutorInstance(executor);
            }
        }
        log.info("{}", protocolConfig);
        return protocolConfig;
    }

    @Bean
    public ClusterConfig clusterconfig(RegistryConfig registryConfig, ApplicationConfig applicationConfig) {
        ClusterConfig clusterConfig = properties.getCluster();
        if (clusterConfig == null) {
            throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "必须配置clusterConfig");
        }
        AbstractLoadBalancer loadBalancer = extensionLoader.load(AbstractLoadBalancer.class, LoadBalanceType.class, clusterConfig.getLoadbalance());
        loadBalancer.updateGlobalConfig(GlobalConfig.builder()
                .applicationConfig(applicationConfig)
                .registryConfig(registryConfig)
                .clusterConfig(clusterConfig)
                .build());

        if (clusterConfig.getFaulttolerance() != null) {
            clusterConfig.setFaultToleranceHandlerInstance(extensionLoader.load(FaultToleranceHandler.class, FaultToleranceType.class, clusterConfig.getFaulttolerance()));
        } else {
            // 默认是failover
            clusterConfig.setFaultToleranceHandlerInstance(FaultToleranceType.FAILOVER.getInstance());
        }
        clusterConfig.setLoadBalanceInstance(loadBalancer);
        log.info("{}", clusterConfig);

        // 注册Filter
        if (loadBalancer instanceof LeastActiveLoadBalancer) {
            extensionLoader.register(Filter.class, "activeLimit", new ActiveLimitFilter());
        }
        return clusterConfig;
    }

    @Bean
    public RPCConsumerBeanPostProcessor rpcConsumerBeanPostProcessor(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        RPCConsumerBeanPostProcessor processor = new RPCConsumerBeanPostProcessor();
        processor.init(applicationConfig, clusterConfig, protocolConfig, registryConfig);
        log.info("RPCConsumerBeanPostProcessor init");
        return processor;
    }

    @Bean
    public RPCProviderBeanPostProcessor rpcProviderBeanPostProcessor(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        RPCProviderBeanPostProcessor processor = new RPCProviderBeanPostProcessor();
        processor.init(applicationConfig, clusterConfig, protocolConfig, registryConfig);
        log.info("RPCProviderBeanPostProcessor init");
        return processor;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // 读取用户目录下的/easy下面的配置文件
        // 内部应用使用枚举单例的方式获取实例，外部应用使用Spring的方式获取实例
        this.extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadResources();
    }
}


