package com.rpc.client.config;


import com.rpc.client.processor.RpcClientProcessor;
import com.rpc.client.proxy.ClientStubProxyFactory;
import com.rpc.core.balance.FullRoundBalance;
import com.rpc.core.balance.LoadBalance;
import com.rpc.core.balance.RandomBalance;
import com.rpc.core.discovery.DiscoveryService;
import com.rpc.core.discovery.RedisDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
public class RpcClientAutoConfiguration {


    @Bean
    public RpcClientProperties rpcClientProperties(Environment environment) {
        // 这里使用Binder.get(environment)从Environment中获取一个Binder实例
        // 这个实例用于绑定配置属性。
        BindResult<RpcClientProperties> result = Binder.get(environment)
                // bind方法用于绑定名为rpc.client的配置属性到RpcClientProperties类。
                // 这个方法会查找所有以rpc.client为前缀的配置属性，
                // 并将它们绑定到RpcClientProperties的实例中。
                .bind("rpc.client", RpcClientProperties.class);
        return result.get();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientStubProxyFactory clientStubProxyFactory(){
        return new ClientStubProxyFactory();
    }

    // 这个注解用于指定一个bean，当有多个相同类型的bean时
    // 这个bean将作为首选。这意味着当Spring容器中存在多个LoadBalance类型的bean时
    // randomBalance方法返回的bean将被优先使用。
    @Primary // 更高优先级
    @Bean(name="loadBalance")
    // 它指定了当Spring应用上下文中不存在相同类型的其他bean时，
    // 当前的bean定义才会生效。
    // 如果已经存在一个LoadBalance类型的bean，
    // 那么randomBalance方法定义的bean将不会被创建。
    @ConditionalOnMissingBean
    // 用于根据配置属性来决定是否创建某个bean。
    // 在这个例子中，它检查rpc.client前缀下的balance属性的值是否为randomBalance，
    // 或者这个属性是否缺失
    // （matchIfMissing = true表示如果属性缺失，条件也视为满足）。
    @ConditionalOnProperty(prefix = "rpc.client",name="balance",havingValue = "randomBalance",matchIfMissing = true)
    public LoadBalance randomBalance(){
        return new RandomBalance();
    }

    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "fullRoundBalance")
    public LoadBalance loadBalance() {
        return new FullRoundBalance();
    }

    @Bean
    @ConditionalOnMissingBean
    // 这个条件注解指定了只有当Spring应用上下文中
    // 存在RpcClientProperties和LoadBalance类型的bean时，
    // 当前的bean定义才会生效。
    @ConditionalOnBean({RpcClientProperties.class,LoadBalance.class})
    public DiscoveryService discoveryService(@Autowired RpcClientProperties properties
                            ,@Autowired LoadBalance loadBalance ){
        return new RedisDiscoveryService(8099,"address","password",1,loadBalance);

    }

    @Bean
    @ConditionalOnMissingBean
    public RpcClientProcessor rpcClientProcessor(@Autowired ClientStubProxyFactory clientStubProxyFactory,
                                                 @Autowired DiscoveryService discoveryService,
                                                 @Autowired RpcClientProperties properties) {
        return new RpcClientProcessor(clientStubProxyFactory, discoveryService, properties);
    }

}
