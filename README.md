# prpc
an rpc framework

## 1 Architecture

![prpc architecture](imgs/prpc-architecture.png)


## 2 Features

- [ ] 注册中心
    - [x] zookeeper
    - [ ] redis
- [ ] 负载均衡
    - [x] Random
    - [x] RoundRobin
    - [ ] WeightRandom
    - [ ] WeightRoundRobin
    - [ ] idc负载
- [ ] 失败重试策略
    - [x] fastfail
    - [ ] fastover
- [ ] 序列化协议
    - [x] protobuf
    - [x] jackson
    - [x] fastjson
    - [ ] hessian2
- [ ] 应用协议兼容
    - [x] dubbo(还存在问题)
    - [ ] http
    - [ ] brpc
- [x] 集成spring boot starter
- [ ] 限流
    - [ ] 计数器算法
    - [ ] 令牌桶算法
    - [ ] 漏桶算法
- [ ] 熔断
- [ ] prpc管理后台

## 3 Quick Start

demo详情可查看prpc-example

- java8及以上
- 依赖zookeeper

### 3.1 引用依赖

服务端和客户端都需要依赖prpc-spring-boot-starter

```xml
<dependency>
    <groupId>com.weaponlin.inf.prpc</groupId>
    <artifactId>prpc-spring-boot-starter</artifactId>
    <version>${latest-version}</version>
</dependency>
```

### 3.2 定义api

```java
public interface HelloApi {
    HelloResponse hello(Long userId, HelloRequest request);
}
```

### 3.3 创建provider(服务提供方)

(1) 引用api依赖并实现

```xml
<dependency>
    <groupId>com.weaponlin.inf.prpc</groupId>
    <artifactId>api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

```java
public class HelloApiImpl implements HelloApi {
    public HelloResponse hello(Long userId, HelloRequest request) {
        HelloResponse res = HelloResponse.builder().greeting(RandomStringUtils.random(request.getSize(), true, true))
                .build();
        return res;
    }
}
```

(2) 在application.yml添加server配置
```yaml
prpc:
  server:                              # 标识为服务端配置
    protocol: prpc                     # 全局配置
    registryCenter:
      naming: zookeeper
      address: 127.0.0.1:2181
    codec: protobuf
    timeout: 30000
    groups:                            # 服务分组配置
      - group: default                 # 服务分组名称
        registryCenter:                # 注册中心
          naming: zookeeper            # 命名方式，eg: zookeeper、redis(待支持)
          address: 127.0.0.1:2181      # 注册中心地址
        codec: protobuf                # 编解码，eg: protobuf、fastjson、jackson、
        protocol: prpc                 # 通信协议，eg: prpc、dubbo(待优化)、brpc(待支持)
        timeout: 30000
        basePackage: com.weaponlin.inf.prpc.api # api所在的包，多个包以逗号分割(待支持)
```

(3) 启动应用
```java
@SpringBootApplication
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }        
}
```

### 3.4 创建consumer(服务消费方)

(1) 引用api
```xml
<dependency>
    <groupId>com.weaponlin.inf.prpc</groupId>
    <artifactId>api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

(2) 创建controller调用api

引用api需要标注 *@ReferenceService* 注解

```java
@RestController
@RequestMapping("")
public class HelloController {

    @ReferenceService
    private HelloApi helloApi;

    @GetMapping("/hello")
    public HelloResponse hello() {
        HelloRequest req = new HelloRequest();
        req.setSize(111);
        req.setMessage("asdfasdfadsf");
        return helloApi.hello(1L, req);
    }
}
```

(3) 在application.yml添加client配置

```yaml
prpc:
  client:                                 # 标识为客户端配置
    protocol: prpc                        # 全局配置
    registryCenter:
      naming: zookeeper
      address: 127.0.0.1:2181
    codec: protobuf
    loadBalance: roundrobin
    cluster: failfast                     # 容错机制
    groups:
      - group: default
        registryCenter:
          naming: zookeeper
          address: 127.0.0.1:2181
        codec: protobuf
        loadBalance: roundrobin           # 负载均衡
        protocol: prpc
        timeout: 30000
        basePackage: com.weaponlin.inf.prpc.api

# web 服务端口
server:
  port: 8081

```

(4) 启动应用

```java
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
``` 

## 4 TODO

一些待优化的事情记录

- [x] 协议优化，分别读写meta、request、response等
- [x] shutdown hook，在服务关闭的时候及时清理zk节点
- [x] zookeeper server节点优化，定义prpc协议，例如: prpc://127.0.0.1:9999?idc=nj&weight=10
- [ ] 心跳检测
- [ ] 添加filter机制，用来支持限流、熔断等功能
- [x] naming service，支持命名方式解析
- [x] config数据结构优化，清晰一些
- [x] 支持多端口启动，例如服务同时需要支持dubbo和prpc协议，需要启动两个端口，分别接收请求
- [x] prpc协议添加magic num
- [ ] client请求进行管理，例如：超时直接抛异常
- [x] 预置系统变量，例如：idc等
- [x] ServiceLoader优化，静态代码块，在使用的时候采取加载初始化，导致获取service太慢了
- [ ] 限制请求大小
- [ ] meta里添加rpc version等信息，方便后续扩展
- [ ] 支持dubbo协议，但是没调用几次provider就会关闭，consumer提示provider关闭了
- [ ] 支持标注@ExportService注解的服务发布
- [ ] 支持多包发布或引用
- [ ] 服务端口可配置化(感觉意义不大)


## 5 User Feedback


