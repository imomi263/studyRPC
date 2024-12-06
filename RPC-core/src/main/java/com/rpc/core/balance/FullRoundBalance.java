package com.rpc.core.balance;

import com.rpc.core.common.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class FullRoundBalance implements LoadBalance {
    private static Logger logger = LoggerFactory.getLogger(FullRoundBalance.class);

    private int index;

    @Override
    public ServiceInfo chooseOne(List<ServiceInfo> services) {
        index=index%services.size();
        return services.get(index++);
    }
}
