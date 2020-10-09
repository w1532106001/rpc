package registry.zookeeper;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author whc
 * @date 2020/10/9
 * @description
 */
@Slf4j
public class ServiceRegisterImpl implements registry.zookeeper.ServiceRegister {


    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {

    }

    public void removeService(String rpcServiceName) {
    }

    public InetSocketAddress discoverService(String serviceName) {
        return null;
    }
}
