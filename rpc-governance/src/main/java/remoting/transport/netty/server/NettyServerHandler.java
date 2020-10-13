package remoting.transport.netty.server;

import enums.RpcResponseCodeEnum;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import remoting.constants.RpcConstants;
import remoting.dto.RpcMessage;
import remoting.dto.RpcRequest;
import remoting.dto.RpcResponse;
import remoting.handler.RpcRequestHandler;

/**
 * @author whc
 * @date 2020/10/13
 * @description
 */
@Component
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private RpcRequestHandler rpcRequestHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                log.info("服务器收到消息:[{}]", msg);
                byte messageType = ((RpcMessage) msg).getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    RpcMessage rpcMessage = new RpcMessage();
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                    ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    //执行目标方法（客户端需要执行的方法）并返回方法结果
                    Object result = rpcRequestHandler.handler(rpcRequest);
                    log.info(String.format("服务器返回: %s", result.toString()));
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        RpcMessage rpcMessage = new RpcMessage();
                        rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                        rpcMessage.setData(rpcResponse);
                        ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        RpcMessage rpcMessage = new RpcMessage();
                        rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                        rpcMessage.setData(rpcResponse);
                        ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        log.error("现在无法写，消息已删除");
                    }
                }
            }

        } finally {
            //释放ByteBuf，否则可能会发生内存泄漏
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 30 秒之内没有收到客户端请求的话就关闭连接
                log.info("发生空闲检查，因此关闭连接");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务捕捉到异常");
        cause.printStackTrace();
        ctx.close();
    }
}
