package core.balance;

import core.common.ServiceInfo;

import java.util.List;

public interface LoadBalance {
    ServiceInfo chooseOne(List<ServiceInfo> services);
}
