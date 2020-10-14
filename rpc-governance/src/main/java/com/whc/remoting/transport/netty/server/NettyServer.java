package com.whc.remoting.transport.netty.server;

import com.whc.config.ShutdownHook;
import com.whc.entity.RpcServiceProperties;
import com.whc.provider.ServiceProvider;
import com.whc.remoting.codec.RpcMessageDecoder;
import com.whc.remoting.codec.RpcMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @author whc
 * @date 2020/10/11 20:08
 */
@Component
public class NettyServer {
    /**
     * 本地端口
     */
    @Value("${netty.port}")
    public int PORT;
    private final ServiceProvider serviceProvider;
    private final NettyServerHandler nettyServerHandler;

    public NettyServer(ServiceProvider serviceProvider, NettyServerHandler nettyServerHandler) {
        this.serviceProvider = serviceProvider;
        this.nettyServerHandler = nettyServerHandler;
    }

    /**
     * 注册服务
     *
     * @param service              服务类
     * @param rpcServiceProperties 服务类信息
     */
    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    public void start() {
        //注册jvm监听事件
        ShutdownHook.getShutdownHook().clearAllRegistryService();
        try {
            //获取本地host
            String host = InetAddress.getLocalHost().getHostAddress();
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                    4
            );
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, nettyServerHandler);
                        }

                    });
            // 绑定端口，同步等待绑定成功
            ChannelFuture f = serverBootstrap.bind(host, PORT).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
