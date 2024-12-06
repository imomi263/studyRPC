package com.rpc.controller;


import com.rpc.api.server.HelloWorldService;
import com.rpc.client.annotation.RpcAutowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloWorldController {

    @RpcAutowired(version = "1.0")
    private HelloWorldService helloWorldService;


    @GetMapping("/hello/world")
    public ResponseEntity<String> pullServiceInfo(@RequestParam("name") String name){

        System.out.println("计算结果为"+helloWorldService.sum(10,20));

        return  ResponseEntity.ok(helloWorldService.sayHello(name));
    }
}
