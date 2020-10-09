package registry.zookeeper;

import enums.RpcErrorMessageEnum;
import exceptions.RpcException;
import loadbalance.LoadBalance;
import loadbalance.RandomLoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import registry.ZKService;
import util.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author whc
 * @date 2020/10/9
 * @description
 */
@Slf4j
public class ZKServiceImpl implements ZKService {

    /**
     * 负载均衡器
     */
    private final LoadBalance loadBalance;

    public ZKServiceImpl() {
        loadBalance = new RandomLoadBalance();
    }

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }

    @Override
    public void removeService(String rpcServiceName) {

    }

    @Override
    public InetSocketAddress findService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        String serviceUrl = loadBalance.selectServiceAddress(serviceUrlList);
        log.info("服务查找成功,服务地址:{}", serviceUrl);
        String[] socketAddressArray = serviceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host,port);
    }
}
