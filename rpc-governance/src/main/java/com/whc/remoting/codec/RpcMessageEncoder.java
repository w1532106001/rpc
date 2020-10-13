package com.whc.remoting.codec;


import com.whc.remoting.constants.RpcConstants;
import com.whc.remoting.dto.RpcMessage;
import com.whc.serialize.ProtoStuffSerializer;
import com.whc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    private Serializer serializer;

    public RpcMessageEncoder() {
        this.serializer = new ProtoStuffSerializer();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf out) {
        out.writeBytes(RpcConstants.MAGIC_NUMBER);
        out.writeByte(RpcConstants.VERSION);
        //留4个长度写全长值
        out.writerIndex(out.writerIndex() + 2);
        //获取消息类型
        byte messageType = rpcMessage.getMessageType();
        out.writeByte(messageType);
        out.writeInt(ATOMIC_INTEGER.getAndIncrement());
        //建立全长
        byte[] bodyBytes = null;
        int fullLength = RpcConstants.HEAD_LENGTH;
        //如果messageType不是心跳消息，则fullLength =头长+体长
        if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            bodyBytes = serializer.serialize(rpcMessage.getData());
            fullLength += bodyBytes.length;
        }
        if (bodyBytes != null) {
            out.writeBytes(bodyBytes);
        }
        int writeIndex = out.writerIndex();
        out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
        out.writeInt(fullLength);
        out.writerIndex(writeIndex);
    }
}

