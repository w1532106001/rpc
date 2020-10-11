package entity;

import lombok.*;

/**
 * @author whc
 * @date 2020/10/11 19:37
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceProperties {
    /**
     * 服务版本号
     */
    private String version;
    /**
     * 当接口有多个实现时按组区分
     */
    private String group;
    /**
     * 服务名
     */
    private String serviceName;

    public String toRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }
}