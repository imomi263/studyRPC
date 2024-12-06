package com.rpc.server;

import com.rpc.core.common.ServiceInfo;
import com.rpc.core.common.ServiceUtil;
import com.rpc.core.register.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;
import com.rpc.server.annotation.RpcService;
import com.rpc.server.cache.LocalServerCache;
import com.rpc.server.config.RpcServerProperties;
import com.rpc.server.transport.RpcServer;

import java.net.InetAddress;

@Slf4j
public class RpcServerProvider implements BeanPostProcessor, CommandLineRunner {

    private RegistryService registryService;

    private RpcServerProperties properties;

    private RpcServer rpcServer;


    public RpcServerProvider(RegistryService registryService, RpcServer rpcServer, RpcServerProperties properties) {
        this.registryService = registryService;
        this.properties = properties;
        this.rpcServer = rpcServer;
    }

    /**
     * 服务启动时 启动rpc的server监听，处理请求
     * @param args
     * @throws Exception
     */

    @Override
    public void run(String... args){
        rpcServer.start(properties.getPort());
        log.info(" Rpc server :{} start, appName :{} , port :{}", rpcServer, properties.getAppName(), properties.getPort());
        //注册服务关闭钩子 服务关闭同时 关闭rpc服务
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {

                // 关闭之后把服务从注册中心上移除
                LocalServerCache.getAll().forEach(it ->{
                    try {
                        RpcService rpcService = it.getClass().getAnnotation(RpcService.class);
                        String serviceName = rpcService.interfaceType().getName();
                        String version = rpcService.version();
                        ServiceInfo serviceInfo = new ServiceInfo();
                        serviceInfo.setServiceName(ServiceUtil.serviceKey(serviceName, version));
                        serviceInfo.setPort(properties.getPort());
                        serviceInfo.setAddress(InetAddress.getLocalHost().getHostAddress());
                        serviceInfo.setAppName(properties.getAppName());
                        registryService.unregister(serviceInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                // 关闭zk
                registryService.destroy();

            }catch (Exception e){
                log.error("", e);
            }

        }));
    }


    /**
     * 每个bean 实例化之后都要来执行的这一步
     * 目的是 暴露服务 注册到 注册中心
     * 容器启动后 自动开启netty 服务处理请求
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //log.info("RpcServerProvider postProcessAfterInitialization");

        // 获取bean上的自定义注解 @RpcService
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);

        //检查是否拿到该注解
        if (rpcService != null) {
            try {
                String serviceName = rpcService.interfaceType().getName();
                String version = rpcService.version();

                // 存入本地缓存
                LocalServerCache.setServerBean(ServiceUtil.serviceKey(serviceName, version), bean);
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setServiceName(ServiceUtil.serviceKey(serviceName, version));
                serviceInfo.setPort(properties.getPort());
                serviceInfo.setAddress(InetAddress.getLocalHost().getHostAddress());
                serviceInfo.setAppName(properties.getAppName());

                //注册服务
                registryService.register(serviceInfo);


            } catch (Exception e) {
                log.error("服务注册过程中出错: {}", e);
            }
        }

        return bean;
    }
}
