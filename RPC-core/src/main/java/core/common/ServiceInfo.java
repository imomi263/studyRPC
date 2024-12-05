package core.common;

import lombok.Data;

import java.io.Serializable;

// 服务信息，要被存到注册中心，需要实现序列化接口
@Data
public class ServiceInfo implements Serializable {

    // 应用名称
    private String appName;
    // 服务名称
    private String serviceName;
    // 版本
    private String version;

    // 地址
    private String address;

    // 端口
    private Integer port;


}

