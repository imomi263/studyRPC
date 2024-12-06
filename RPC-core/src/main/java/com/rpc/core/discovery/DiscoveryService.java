package com.rpc.core.discovery;


import com.rpc.core.common.ServiceInfo;

/**
 * 服务发现接口
 */
public interface DiscoveryService {
    ServiceInfo discovery(String serviceName)throws Exception;

}
