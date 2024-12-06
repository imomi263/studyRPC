package com.rpc.client.config;

import lombok.Data;

@Data
public class RpcClientProperties {
    /**
     * 负载均衡策略
     */
    private String balance;


    /**
     *  服务发现地址
     */
    private String discoveryAddr = "127.0.0.1:2181";

    /**
     *  服务调用超时
     */
    private Integer timeout;

    /**
     *  序列化
     */
    private String serialization;

}
