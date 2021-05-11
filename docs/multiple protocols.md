# 多协议支持

1.cs通信数据结构一致
2.注册中心对应的存储数据结构要一致(client订阅后可以按其协议进行解析)


应用层协议：dubbo、brpc、thrift、http
注册中心：none、zookeeper、redis、etcd
序列化协议：protobuf、json、hession

可混合交叉使用



