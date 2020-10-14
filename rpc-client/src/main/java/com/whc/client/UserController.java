package com.whc.client;

import com.whc.annotation.RpcReference;
import com.whc.entity.User;
import com.whc.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author whc
 * @date 2020/10/13 22:02
 */
@RestController
@RequestMapping("user")
public class UserController {

    @RpcReference(version = "1", group = "test")
    private UserService userService;

    @PostMapping("/login")
    public String login(String username, String password) {
        return userService.login(new User(username, password, 18));
    }
}
