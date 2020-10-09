package registry.zookeeper;

import java.net.InetSocketAddress;

/**
 *
 */
public interface ServiceRegister {
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
     * @return 下线的服务信息.
     */
    void removeService(String rpcServiceName);

    /**
     * 拉取服务注册信息.
     *
     * @return 服务注册信息.
     * @param serviceName 服务名称
     */
    InetSocketAddress discoverService(String serviceName);

}
