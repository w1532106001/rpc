package com.whc.remoting.dto;


import lombok.*;

/**
 * @author whc
 * @date 2020/10/13
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    //rpc 消息类型
    private byte messageType;
    //请求id
    private int requestId;
    //请求数据
    private Object data;

}
