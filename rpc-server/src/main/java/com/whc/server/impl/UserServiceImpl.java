package com.whc.server.impl;

import com.whc.annotation.RpcService;
import com.whc.entity.User;
import com.whc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author whc
 * @date 2020/10/13
 * @description
 */
@Slf4j
@RpcService(version = "1", group = "test")
@Component
public class UserServiceImpl implements UserService {
    @Override
    public String login(User user) {
        log.info("登录成功:[{}]", user.toString());
        return user.toString();
    }
}
