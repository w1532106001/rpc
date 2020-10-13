package com.whc.remoting.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whc
 * @date 2020/10/13
 * @description
 */
@Slf4j
@Component
public class ChannelProvider {
    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    @Autowired
    private NettyClient nettyClient;

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        Channel channel = channelMap.get(key);
        if (channel != null) {
            if (channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(key);
            }
        }
        channel = nettyClient.doConnect(inetSocketAddress);
        channelMap.put(key, channel);
        return channel;
    }
}
