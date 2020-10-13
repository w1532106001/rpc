package com.whc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author whc
 * @createTime 2020年10月13日
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {

    SUCCESS(200, "远程调用成功"),
    FAIL(500, "远程调用失败");
    private final int code;

    private final String message;

}
