package com.whc.remoting.codec;


import com.whc.remoting.constants.RpcConstants;
import com.whc.remoting.dto.RpcMessage;
import com.whc.remoting.dto.RpcRequest;
import com.whc.remoting.dto.RpcResponse;
import com.whc.serialize.ProtoStuffSerializer;
import com.whc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;


@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    private Serializer serializer;

    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
        this.serializer = new ProtoStuffSerializer();
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                return decodeFrame(frame);
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {
        //注意：必须按顺序读取ByteBuf 读取前4位（即幻数）并进行比较
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("未知的魔术码: " + Arrays.toString(tmp));
            }
        }
        // 读取版本号并进行比较
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("版本不兼容" + version);
        }
        int fullLength = in.readInt();
        // 构建RpcMessage对象
        byte messageType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .requestId(requestId)
                .messageType(messageType).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
        } else if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
        } else {
            int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
            if (bodyLength > 0) {
                byte[] bs = new byte[bodyLength];
                in.readBytes(bs);
                if (messageType == RpcConstants.REQUEST_TYPE) {
                    RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                    rpcMessage.setData(tmpValue);
                } else {
                    RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                    rpcMessage.setData(tmpValue);
                }
            }
        }
        return rpcMessage;
    }

}

