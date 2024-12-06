package com.rpc.server.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rpc.server")
public class RpcServerProperties {
    //服务端口
    private Integer port=8090;
    // 服务名称
    private String appName;
    // 配置中心地址
    private String registryAddr="127.0.0.1:2181";
}
