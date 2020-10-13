package com.whc.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author whc
 */
@ComponentScan(basePackages = {"com.whc"})
@SpringBootApplication
public class RpcClientApplication {


    public static void main(String[] args) {
        SpringApplication.run(RpcClientApplication.class, args);
    }

}
