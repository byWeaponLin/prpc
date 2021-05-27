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


