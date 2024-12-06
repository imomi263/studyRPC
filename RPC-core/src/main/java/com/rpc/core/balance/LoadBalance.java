package com.rpc.core.balance;

import com.rpc.core.common.ServiceInfo;

import java.util.List;

public interface LoadBalance {
    ServiceInfo chooseOne(List<ServiceInfo> services);
}
