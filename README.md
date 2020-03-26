# 轻量级分布式RPC框架

<a name="d089eb8a"></a>
## 使用示例
- 下载源码，本地编译，deploy
- 在需要引入的springboot项目中添加rpc-spring-boot-starter的maven依赖
```
<dependency>
	<groupId>com.github.zhaoli</groupId>
	<artifactId>rpc-spring-boot-starter</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```


- spring配置文件application.properties



```
#应用名称 
rpc.application.name=app-2
#序列化，可选项jdk、json、hession、protostuff
rpc.application.serialize=jdk
#动态代理，可选项cglib、jdk、javassist
rpc.application.proxy=cglib
#传输协议，可选项tcp，http，injvm(本地)
rpc.protocol.type=tcp
#netty端口
rpc.protocol.port=8000
#服务端线程数
rpc.protocol.executor.server.threads=100
#服务端线程池实现，可选项disruptor、threadpool
rpc.protocol.executor.server.type=disruptor
#注册中心地址
rpc.registry.address=127.0.0.1:2181
#注册中心类型，可选项 zk、curator
rpc.registry.type=curator

#客户端负载均衡策略，可选项LEAST_ACTIVE、RANDOM、CONSISTENT_HASH、ROUND_ROBIN、WEIGHTED_RANDOM
rpc.cluster.loadbalance=LEAST_ACTIVE
#客户端失败策略，可选项failover、failfast、failsafe
rpc.cluster.faulttolerance=failover
#客户端线程池实现，可选项disruptor、threadpool
rpc.protocol.executor.client.type=threadpool
```


- 服务提供者provider
```
@RPCService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello, " + name;
    }
}
```


- 服务消费者consumer
```

@Component
public class SayHelloService {

    @RPCReference
    private HelloService helloService;


    public void test() {
        helloService.hello("I am consumer");
    }
}
```

<br />

<a name="WCP1z"></a>
## 功能列表


- 基于Netty实现长连接通信，包括心跳保持、断线重连、解决粘包半包等
- 基于Zookeeper实现分布式服务注册与发现
- 实现了轮询、随机、加权随机等负载均衡算法，以及FailOver、FailFast、FailSafe等多种集群容错方式
- 实现了同步、异步、回调、Oneway等多种调用方式
- 实现了TCP、HTTP、InJvm等多种协议
- 实现了客户端侧的Filter，并基于此实现了LeastActive负载均衡算法
- 实现了简易扩展点，泛化调用等功能
- 基于jdk、javassist、cglib的动态代理
- 实现了Spring Boot Starter


<br />

<a name="cWFHb"></a>
## 层级介绍
<a name="cHsQm"></a>
### config
配置层。<br />对应的核心类：

- ReferenceConfig（对应一个服务接口的引用，持有接口代理实例）
- ServiceConfig（对应一个服务接口的暴露，持有接口实现类实例）
- GlobalConfig（全局配置）
  - ApplicationConfig（应用配置，持有ProxyFactory实例、Serializer实例）
  - RegistryConfig（注册中心配置，持有ServiceRegistry实例）
  - ProtocolConfig（协议配置，持有Protocol实例、Executor实例）
  - ClusterConfig（集群配置，持有LoadBalancer实例、FaultToleranceHandler实例）



<a name="GWD8B"></a>
### proxy
代理层，主要是为ReferenceConfig生成接口的代理实例（抽象为Invoker，底层是RPC调用），以及为ServiceConfig生成接口的代理实例（抽象为Invoker，底层直接委托给实现类实例）。<br />

<a name="1kMg3"></a>
### registry
注册中心层，主要是服务注册与服务发现。<br />

<a name="90Eeg"></a>
### cluster
集群层，主要是将一个接口的集群实现对外暴露为单个实现，屏蔽集群的细节。在集群内部主要是做负载均衡以及集群容错。<br />

<a name="QSpG5"></a>
### protocol、invocaiton、filter
协议层。<br />

<a name="IONi7"></a>
### transport
通信层，需要配合协议层，自定义协议实现需要相应的自定义通信实现。<br />
<br />
<br />

<a name="7fe8fe71"></a>
## 扩展点介绍

<br />扩展点：用户可以为某个接口添加自己的实现，在不改变框架源码的前提下，对部分实现进行定制。

- RPCProxyFactory（目前有jdk动态代理实现、Javassist实现和cglib实现）
- ServiceRegistry（目前有基于org.apache.zookeeper和org.apache.curator的两种注册中心实现）
- LoadBalancer（必须继承自AbstractLoadBalancer，目前有随机、加权随机、轮询、一致性哈希、最小活跃度五种实现）
- FaultToleranceHandler（目前有FailOver、FailFast、FailSafe三种实现）
- Protocol（目前有TCP、HTTP、InJvm三种实现）<br />
- Filter（目前有一个为了实现LeastActive算法的ActiveLimitFilter实现）
- Serialzer （目前有hession，jdk，json，protostuff四种实现）


<br />
<br />

<a name="26ef3c12"></a>
## 如何扩展一个扩展点


- Filter
  - 在resources目录下创建一个easy目录，然后创建一个名为com.github.zhaoli.rpc.easy.filter.Filter的文件，文件内容是k=v的格式，k是一个实现类的别名，v是实现类的全类名，比如：bizlog=BizLogFilter。这个和Dubbo的实现非常像。
- Serializer
  - 同上，文件名为com.github.zhaoli.rpc.easy.serialize.api.Serializer，文件内容也是k=v的格式。另外，需要修改application.properties中的rpc.application.serialize={你在文件里写的k}。
