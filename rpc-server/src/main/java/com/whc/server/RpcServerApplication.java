package com.whc.server;

import entity.RpcServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import remoting.transport.netty.server.NettyServer;
import service.UserService;

import javax.annotation.Resource;

@SpringBootApplication
public class RpcServerApplication {

    @Resource
    private static NettyServer nettyServer;
    @Resource
    private static UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);
        startRpcServer();
    }

    public static void startRpcServer() {
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test").version("version").build();
        nettyServer.registerService(userService, rpcServiceProperties);
        nettyServer.start();
    }
}
