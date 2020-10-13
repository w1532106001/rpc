package com.whc.server.impl;

import com.whc.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import service.UserService;

/**
 * @author whc
 * @date 2020/10/13
 * @description
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Override
    public void login(User user) {
        log.info("登录成功:[{}]", user.toString());
    }
}
