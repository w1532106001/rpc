package remoting.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import registry.ZKService;
import remoting.constants.RpcConstants;
import remoting.dto.RpcMessage;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * transport rpcRequest based on netty.
 *
 * @author shuang.kou
 * @createTime 2020年05月29日 11:34:00
 */
@Slf4j
@Component
public class NettyClientTransport {

    @Autowired
    private ZKService zkService;

    @Autowired
    private UnprocessedRequests unprocessedRequests;
    @Autowired
    private ChannelProvider channelProvider;


    public CompletableFuture<RpcResponse<Object>> sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // 建立rpcServiceName
        String rpcServiceName = rpcRequest.toRpcProperties().toRpcServiceName();
        // 获取服务端地址
        InetSocketAddress inetSocketAddress = zkService.findService(rpcServiceName);
        // 获取服务器地址相关通道
        Channel channel = channelProvider.getChannel(inetSocketAddress);
        if (channel != null && channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = new RpcMessage();
            rpcMessage.setData(rpcRequest);
            rpcMessage.setMessageType(RpcConstants.REQUEST_TYPE);
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("客户端消息发送成功: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("客户端消息发送失败:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }

}


