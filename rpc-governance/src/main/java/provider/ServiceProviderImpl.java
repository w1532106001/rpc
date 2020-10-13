package provider;

import entity.RpcServiceProperties;
import enums.RpcErrorMessageEnum;
import exceptions.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import registry.ZKService;
import registry.zookeeper.ZKServiceImpl;
import remoting.transport.netty.server.NettyServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whc
 * @date 2020/10/11 19:39
 */
@Slf4j
@Component
public class ServiceProviderImpl implements ServiceProvider {



    /**
     * key: rpc服务名(接口名 + 版本号 + 分组名)
     * value: 服务类
     */
    private final Map<String, Object> serviceMap;
    private final ZKService zkService;

    public ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        zkService = new ZKServiceImpl();
    }

    @Override
    public void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties) {
        //查询是否存在此服务
        if (serviceMap.containsKey(rpcServiceProperties.toRpcServiceName())) {
            log.info("服务已存在,rpc服务名：[{}], 实现类：[{}]", rpcServiceProperties.toRpcServiceName(), service.getClass().getInterfaces());
            return;
        }
        serviceMap.put(rpcServiceProperties.toRpcServiceName(), service);
        log.info("服务添加成功,rpc服务名：[{}], 实现类：[{}]", rpcServiceProperties.toRpcServiceName(), service.getClass().getInterfaces());
    }

    @Override
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        Object service = serviceMap.get(rpcServiceProperties.toRpcServiceName());
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {
        try {
            //获取host地址
            String host = InetAddress.getLocalHost().getHostAddress();
            //获取服务类相关信息
            Class<?> serviceInterface = service.getClass().getInterfaces()[0];
            //获取服务名
            String serviceName = serviceInterface.getCanonicalName();
            rpcServiceProperties.setServiceName(serviceName);
            this.addService(service, serviceInterface, rpcServiceProperties);
            //注册服务
            zkService.registerService(rpcServiceProperties.toRpcServiceName(),new InetSocketAddress(host, NettyServer.PORT));
        } catch (UnknownHostException e) {
            log.error("获取host地址发生异常", e);
            e.printStackTrace();
        }
    }

    @Override
    public void publishService(Object service) {
        this.publishService(service, RpcServiceProperties.builder().group("").version("").build());
    }
}
