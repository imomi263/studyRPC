package client.proxy;

import client.proxy.ClientStubInvocationHandler;
import client.config.RpcClientProperties;
import core.discovery.DiscoveryService;
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
        return (T) objectCache.computeIfAbsent(tClass, clz -> Proxy.newProxyInstance(clz.getClassLoader(),
                new Class[]{clz},
                new ClientStubInvocationHandler(discoveryService, properties, clz, version)));
    }
}
