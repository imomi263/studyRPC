package server.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalServerCache {
    private static final Map<String,Object> serverCacheMap=new HashMap<>();
    public static void setServerBean(String serverName,Object server){
        // merge方法: 如果该key没有存在与map的新增，如果存在，则按照指定的规则选择是否替换
        serverCacheMap.merge(serverName,server,(Object oldObj,Object newObj) ->newObj);
    }

    public static Object get(String serverName){
        return serverCacheMap.get(serverName);
    }

    public static List<Object> getAll(){
        return Arrays.asList(serverCacheMap.values().toArray());
    }
}
