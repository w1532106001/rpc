package com.whc.client;

import com.whc.remoting.client.NettyClientTransport;
import com.whc.remoting.dto.RpcRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author whc
 * @date 2020/10/13 22:02
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private NettyClientTransport nettyClientTransport;

    public String login(String username, String password) {
        RpcRequest rpcRequest = new RpcRequest();
        nettyClientTransport.sendRpcRequest().;
    }
}
