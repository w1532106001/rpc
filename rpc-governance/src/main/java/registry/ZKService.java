package registry;

import java.net.InetSocketAddress;


/**
 * @author whc
 * @date 2020/10/9
 * @description
 */
public interface ZKService {
    /**
     * 注册服务
     * @param rpcServiceName 服务名称
     * @param inetSocketAddress 服务地址
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);



    /**
     * 下线服务，主动下线服务.
     *
     * @param rpcServiceName 下线服务名称.
     */
    void removeService(String rpcServiceName);

    /**
     * 查找服务注册信息.
     *
     * @return 服务注册信息.
     * @param rpcServiceName 服务名称
     */
    InetSocketAddress findService(String rpcServiceName);

}
