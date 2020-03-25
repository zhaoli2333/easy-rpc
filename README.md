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


<a name="WCP1z"></a>
## 功能列表


- 基于Netty实现长连接通信，包括心跳保持、断线重连、解决粘包半包等
- 基于Zookeeper实现分布式服务注册与发现
- 实现了轮询、随机、加权随机等负载均衡算法，以及FailOver、FailFast、FailSafe等多种集群容错方式
- 实现了同步、异步、回调、Oneway等多种调用方式
- 实现了TCP、HTTP、InJvm等多种协议
- 实现了客户端侧的Filter，并基于此实现了LeastActive负载均衡算法
- 实现了简易扩展点，泛化调用等功能
- 基于jdk和javassist的动态代理
- 实现了Spring Boot Starter



<a name="7fe8fe71"></a>
## 扩展点介绍

<br />扩展点：用户可以为某个接口添加自己的实现，在不改变框架源码的前提下，对部分实现进行定制。<br />
<br />最常见的是Filter扩展点。<br />
<br />

<a name="proxy"></a>
### proxy

<br />代理层，主要是为ReferenceConfig生成接口的代理实例（抽象为Invoker，底层是rpc调用），以及为ServiceConfig生成接口的代理实例（抽象为Invoker，底层直接委托给实现类实例）。<br />
<br />对应的扩展点：<br />

- RPCProxyFactory（目前有jdk动态代理实现和Javassist实现）



<a name="registry"></a>
### registry

<br />注册中心层，主要是服务注册与服务发现。<br />
<br />对应的扩展点：<br />

- ServiceRegistry（目前有基于org.apache.zookeeper和org.apache.curator的两种注册中心实现）



<a name="cluster"></a>
### cluster

<br />集群层，主要是将一个接口的集群实现对外暴露为单个实现，屏蔽集群的细节。在集群内部主要是做负载均衡以及集群容错。<br />
<br />对应的扩展点：<br />

- LoadBalancer（必须继承自AbstractLoadBalancer，目前有随机、加权随机、轮询、一致性哈希、最小活跃度五种实现）
- FaultToleranceHandler（目前有FailOver、FailFast、FailSafe三种实现）



<a name="f89764aa"></a>
### protocol、invocaiton、filter

<br />协议层，也是最核心的一层。<br />
<br />对应的扩展点：<br />

- Protocol（目前有TCP、HTTP、InJvm三种实现，需要实现响应的Invoker）
- Filter（目前有一个为了实现LeastActive算法的ActiveLimitFilter实现）
- Invocation（一般不需要扩展，目前有同步、异步、Oneway、Callback四种实现）


<br />

<a name="9b2674de"></a>
### serialize

<br />序列化器。<br />
<br />对应的扩展类：<br />

- Serialzer （目前有hession，jdk，json，protostuff四种实现）


<br />

<a name="26ef3c12"></a>
## 如何扩展一个扩展点


- Filter
  - 在resources目录下创建一个easy目录，然后创建一个名为com.github.zhaoli.rpc.easy.filter.Filter的文件，文件内容是k=v的格式，k是一个实现类的别名，v是实现类的全类名，比如：bizlog=BizLogFilter。这个和Dubbo的实现非常像。
- Serializer
  - 同上，文件名为com.github.zhaoli.rpc.easy.serialize.api.Serializer，文件内容也是k=v的格式。另外，需要修改application.properties中的rpc.application.serialize={你在文件里写的k}。
