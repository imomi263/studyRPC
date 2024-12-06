package com.rpc.provider.service;

import com.rpc.api.server.HelloWorldService;
import com.rpc.server.annotation.RpcService;
import org.springframework.context.annotation.Bean;


@RpcService(interfaceType = HelloWorldService.class, version = "1.0")
public class HelloWorldServiceImpl implements HelloWorldService {

    @Override
    public String sayHello(String name) {
        System.out.println("==============服务端执行开始================");

        try{
            // Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("==============服务端执行开始================");

        return String.format("您好：%s, rpc 调用成功", name);
    }

    @Override
    public Integer sum(Integer a, Integer b) {
        System.out.println("调用方传入的参数: a="+a+"b="+b);
        return a+b;
    }
}
