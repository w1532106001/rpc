package service;

import com.whc.entity.User;

/**
 * @author whc
 * @date 2020/10/11 20:52
 */
public interface UserService {
    /**
     * 登录
     *
     * @param user 用户类
     */
    void login(User user);
}
