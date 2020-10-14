package com.whc.server;

import com.whc.entity.RpcServiceProperties;
import com.whc.remoting.transport.netty.server.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

/**
 * @author whc
 */
@ComponentScan(basePackages = {"com.whc"})
@SpringBootApplication
public class RpcServerApplication implements CommandLineRunner {

    @Resource
    private NettyServer nettyServer;
//    @RpcReference(version = "1",group = "test")
//    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test").version("version").build();
//        nettyServer.registerService(userService, rpcServiceProperties);
        nettyServer.start();

    }
}
