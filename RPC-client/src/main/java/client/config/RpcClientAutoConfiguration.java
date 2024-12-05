package client.config;


import client.proxy.ClientStubProxyFactory;
import core.balance.FullRoundBalance;
import core.balance.LoadBalance;
import core.balance.RandomBalance;
import core.discovery.DiscoveryService;
import core.discovery.RedisDiscoveryService;
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
        BindResult<RpcClientProperties> result = Binder.get(environment).bind("rpc.client", RpcClientProperties.class);
        return result.get();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientStubProxyFactory clientStubProxyFactory(){
        return new ClientStubProxyFactory();

    }




}
