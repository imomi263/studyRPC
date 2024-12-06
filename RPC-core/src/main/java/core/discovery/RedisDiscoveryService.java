package core.discovery;

import com.alibaba.fastjson2.JSONObject;
import core.balance.LoadBalance;
import core.balance.RandomBalance;
import core.common.ServiceInfo;
import io.netty.util.internal.StringUtil;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.stream.Collectors;

public class RedisDiscoveryService implements DiscoveryService {

    private final String BASE_KEY="rpc";
    private int port=6379;
    private String address="127.0.0.1";
    private String password="";
    private int dbs=1;
    private LoadBalance loadBalance;

    public RedisDiscoveryService() {
        this.loadBalance=new RandomBalance();

    }

    public RedisDiscoveryService(int port,String address, String password, int dbs) {
        this.port=port;
        this.address=address;
        this.password=password;
        this.dbs=dbs;
    }

    public RedisDiscoveryService(int port,String address, String password,int dbs,LoadBalance loadBalance) {
        this.port=port;
        this.address=address;
        this.password=password;
        this.loadBalance=loadBalance;
        this.dbs=dbs;
    }


    @Override
    public ServiceInfo discovery(String serviceName) throws Exception {

        Jedis jedis=new Jedis(address,port);
        if(!StringUtil.isNullOrEmpty(password)){
            jedis.auth(password);
        }

        // jedis.set("key","value"); 放入值

        List<String> serviceInfoJsons=jedis.hvals(BASE_KEY+serviceName);
        // Redis里面存了服务的名称，从redis中取出来返回一个服务
        List<ServiceInfo> res=serviceInfoJsons.stream()
                // 调用静态方法把字符串转换成JSONObject对象
                .map(JSONObject::parseObject)
                // 把JSONObject对象转成ServiceInfo类
                .map(jsonObject -> jsonObject.toJavaObject(ServiceInfo.class))
                .collect(Collectors.toList());
        System.out.println(res);

        return CollectionUtils.isEmpty(res)? null:loadBalance.chooseOne(res);
    }
}

