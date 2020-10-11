package service;

import entity.Message;

/**
 * @author whc
 * @date 2020/10/11 20:51
 */
public interface MessageService {
    /**
     * 打印message信息
     * @param message 消息类
     */
    void hello(Message message);
}
