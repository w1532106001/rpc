package com.whc.remoting.dto;

import com.whc.entity.RpcServiceProperties;
import lombok.*;

/**
 * @author whc
 * @date 2020/10/11 20:57
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest {
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数数组
     */
    private Object[] parameters;
    /**
     * 参数类型数组
     */
    private Class<?>[] paramTypes;
    /**
     * 版本号
     */
    private String version;
    /**
     * 组名
     */
    private String group;

    public RpcServiceProperties toRpcProperties() {
        return RpcServiceProperties.builder().serviceName(this.getInterfaceName())
                .version(this.getVersion())
                .group(this.getGroup()).build();
    }
}
