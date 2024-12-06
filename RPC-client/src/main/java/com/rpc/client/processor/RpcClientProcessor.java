package com.rpc.client.processor;

import com.rpc.client.annotation.RpcAutowired;
import com.rpc.client.config.RpcClientProperties;
import com.rpc.client.proxy.ClientStubProxyFactory;
import com.rpc.core.discovery.DiscoveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @Classname RpcClientProcessor
 * @Description bean 后置处理器 获取所有bean
 * 判断bean字段是否被 {@link RpcAutowired } 注解修饰
 * 动态修改被修饰字段的值为代理对象 {@link ClientStubProxyFactory}
 */
@Slf4j
public class RpcClientProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ClientStubProxyFactory clientStubProxyFactory;

    private DiscoveryService discoveryService;

    private RpcClientProperties rpcClientProperties;

    private ApplicationContext applicationContext;

    public RpcClientProcessor(ClientStubProxyFactory clientStubProxyFactory,
                              DiscoveryService discoveryService, RpcClientProperties rpcClientProperties) {
        this.clientStubProxyFactory = clientStubProxyFactory;
        this.discoveryService = discoveryService;
        this.rpcClientProperties = rpcClientProperties;

    }

    @Override
    // 它接收一个beanFactory参数，这个参数包含了所有的bean定义，允许我们在bean创建之前进行处理。
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        log.info("postProcessBeanFactory1111111111");
        for(String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            // 通过bean名称获取对应的BeanDefinition对象。
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);

            // 从BeanDefinition对象中获取bean的全限定类名。
            String beanClassName = beanDefinition.getBeanClassName();

            if(beanClassName != null) {
                // 使用ClassUtils工具类和当前类的类加载器解析类名，获取Class对象。
                Class<?> clazz= ClassUtils.resolveClassName(beanClassName, this.getClass().getClassLoader());

                // 使用ReflectionUtils工具类的doWithFields方法遍历clazz类的所有字段。
                ReflectionUtils.doWithFields(clazz,field -> {
                    // 检查字段是否有RpcAutowired注解。
                    RpcAutowired rpcAutowired = AnnotationUtils.getAnnotation(field, RpcAutowired.class);
                    if(rpcAutowired != null) {
                        //log.info("beanClassName        "+ rpcAutowired.toString());
                        // 从Spring应用上下文中获取当前类的bean实例。
                        Object bean=applicationContext.getBean(clazz);
                        // 设置字段的访问权限，以便可以访问私有字段
                        field.setAccessible(true);
                        // 使用ReflectionUtils的setField方法将RPC服务客户端代理设置到字段中。
                        ReflectionUtils.setField(field,bean,clientStubProxyFactory.getProxy(
                                field.getType(),rpcAutowired.version(),discoveryService,rpcClientProperties
                        ));
                    }

                });
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
