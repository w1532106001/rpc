package remoting.transport.netty.server;

import config.ShutdownHook;
import entity.RpcServiceProperties;
import provider.ServiceProvider;
import provider.ServiceProviderImpl;

/**
 * @author whc
 * @date 2020/10/11 20:08
 */
public class NettyServer {
    /**
     * 本地端口
     */
    public static final int PORT = 9999;
    private final ServiceProvider serviceProvider = new ServiceProviderImpl();

    /**
     * 注册服务
     * @param service 服务类
     * @param rpcServiceProperties 服务类信息
     */
    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    public void start() {
        //注册jvm监听事件
        ShutdownHook.getShutdownHook().clearAllRegistryService();

    }
}
