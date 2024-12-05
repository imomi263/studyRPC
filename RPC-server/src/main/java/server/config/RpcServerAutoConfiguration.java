package server.config;

import core.register.RedisRegistryService;
import core.register.RegistryService;
import server.RpcServerProvider;
import server.transport.NettyRpcServer;
import server.transport.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcServerAutoConfiguration {

    @Autowired
    private RpcServerProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public RegistryService registryService() {

       // return new ZookeeperRegistryService(properties.getRegistryAddr());
        return new RedisRegistryService(8099,"110.41.139.215","p@ssw0rd",1);
    }

    @Bean
    @ConditionalOnMissingBean(RpcServer.class)
    public RpcServer RpcServer() {
        return new NettyRpcServer();
    }

    @Bean
    @ConditionalOnMissingBean(RpcServerProvider.class)
    public RpcServerProvider rpcServerProvider(@Autowired RegistryService registryService,
                                               @Autowired RpcServer rpcServer,
                                               @Autowired RpcServerProperties rpcServerProperties){
        return new RpcServerProvider(registryService, rpcServer, rpcServerProperties);
    }

}
