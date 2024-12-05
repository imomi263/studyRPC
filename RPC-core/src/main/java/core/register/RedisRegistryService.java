package core.register;

import com.alibaba.fastjson.JSONObject;
import core.common.ServiceInfo;
import io.netty.util.internal.StringUtil;
import redis.clients.jedis.Jedis;

public class RedisRegistryService implements RegistryService {

    private final String BASE_KEY="rpc";
    private int port=6379;
    private String address="127.0.0.1";
    private String password="";
    private int dbs=1;

    public RedisRegistryService() {

    }

    public RedisRegistryService(int prot, String address) {
        this.port = prot;
        this.address = address;
    }

    public RedisRegistryService(int port, String address, String password, int dbs) {
        this.port=port;
        this.address=address;
        this.password=password;
        this.dbs=dbs;

    }

    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {

        Jedis jedis=new Jedis(address,port);
        if(!StringUtil.isNullOrEmpty(password)){
            jedis.auth(password);
        }
        String serviceInfoJson= JSONObject.toJSONString(serviceInfo);
        jedis.hset(BASE_KEY+serviceInfo.getServiceName(),serviceInfo.getAddress()+serviceInfo.getPort(), serviceInfoJson);
        //todo 考虑过期问题 可以弄个心跳检测包
    }

    @Override
    public void unregister(ServiceInfo serviceInfo) throws Exception {
        Jedis jedis=new Jedis(address,port);
        if(!StringUtil.isNullOrEmpty(password)){
            jedis.auth(password);
        }
        jedis.hdel(BASE_KEY+serviceInfo.getServiceName(),serviceInfo.getAddress()+serviceInfo.getPort());
    }

    @Override
    public void destroy() throws Exception {

    }
}
