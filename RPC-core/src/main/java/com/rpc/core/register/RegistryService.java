package com.rpc.core.register;

import com.rpc.core.common.ServiceInfo;

public interface RegistryService {

    void register(ServiceInfo serviceInfo) throws Exception;

    void unregister(ServiceInfo serviceInfo) throws Exception;

    void destroy() throws Exception;

}
