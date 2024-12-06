package com.rpc.client.proxy;

import com.rpc.client.config.RpcClientProperties;
import com.rpc.core.discovery.DiscoveryService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClientStubProxyFactory {
    private Map<Class<?>, Object> objectCache = new HashMap<>();

    /**
     * 获取代理对象
     */

    public <T> T getProxy(Class<T> tClass, String version, DiscoveryService discoveryService, RpcClientProperties properties) {

        // 这个方法使用computeIfAbsent方法从缓存objectCache中获取或创建代理对象。
        // 如果缓存中不存在，则使用Proxy.newProxyInstance方法创建一个新的代理实例。

        return (T) objectCache.computeIfAbsent(tClass, clz -> Proxy.newProxyInstance(clz.getClassLoader(),
                // 这是Proxy.newProxyInstance方法的第二个参数，它是一个类数组，指定了代理对象需要实现的接口
                new Class[]{clz},

                // 这是Proxy.newProxyInstance方法的第三个参数，它是一个InvocationHandler，
                // 用于处理对代理对象方法的调用。这里使用的是ClientStubInvocationHandler，
                // 它会在调用时使用discoveryService来发现服务提供者，
                // 然后根据properties中的配置和version来调用正确的服务方法。

                new ClientStubInvocationHandler(discoveryService, properties, clz, version)));
    }
}
