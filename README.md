## RPC-core
RPC的核心组件包含了
1. balance（复杂均衡）
2. codec（解码器和编码器）
3. common（主要包含了请求和相应格式、服务信息等）
4. discovery（主要是发现服务的接口与实现）
5. exception（定义的一些异常）
6. protocal定义了消息头和消息体的格式与一些消息相关的常量
7. register服务注册
8. serialization包含了序列化反序列化的接口与实现。



## RPC-server
RPC服务需要用到的
1. annotation: 定义了服务器端的注解
2. cache：内部注册服务的bean的缓存
3. config：Rpcserver的配置类，这里写了很多server的bean，自动注册到spring里面
4. handler：被设置到服务器里面，处理要返回的response
5. transport: 设置服务器（服务器的编码和译码、消息处理器等）
6. RpcServerProvider： 把有注解的服务注册到注册中心（在这里我用redis）

## RPC-Client
1. annotation: 客户端需要用到的注解
2. cache：客户端消息缓存
3. config：类似于服务器端的config
4. handler：消息处理，把消息放到缓存里面
5. processor：对于一个指定的bean对象，找到它的一个字段（field），然后为这个字段设置一个代理对象。
6. proxy：注册代理
7. transport: 客户端的服务器

要有一个提供服务的服务器，还有一个客户端服务器，还有一个注册服务的服务器(redis)

参考：https://gitee.com/dj_zhaixing/zx-rpc