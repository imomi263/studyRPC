package core.balance;

import core.common.ServiceInfo;

import java.util.List;
import java.util.Random;

public class RandomBalance implements LoadBalance{

    private static Random random = new Random();

    @Override
    public ServiceInfo chooseOne(List<ServiceInfo> services) {
        return services.get(random.nextInt(services.size()));
    }
}
