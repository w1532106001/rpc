package com.whc.util;

import com.whc.constants.RpcConstant;
import com.whc.exceptions.RpcException;
import com.whc.utils.PropertiesFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whc
 * @date 2020/10/9
 * @description
 */
@Slf4j
public final class CuratorUtils {

    /**
     * 休眠时间
     */
    public static final int SLEEP_TIME = 1000;
    /**
     * 重试次数
     */
    public static final int MAX_RETRIES = 3;
    /**
     * 父地址
     */
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    /**
     * 服务地址列表
     */
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    /**
     * 注册地址列表
     */
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;
    /**
     * 默认zk连接地址
     */
    private static String defaultZookeeperAddress = "127.0.0.1:2181";

    /**
     * 创建持久节点
     *
     * @param zkClient zk客户端
     * @param path     地址
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("节点已存在,节点:[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("节点创建成功,节点：[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 删除持久化节点
     *
     * @param zkClient       zk客户端
     * @param rpcServiceName 服务名
     */
    public static void deletePersistentNode(CuratorFramework zkClient, String rpcServiceName) {

    }

    /**
     * @param zkClient       zk客户端
     * @param rpcServiceName rpc服务名
     * @return 根据rpc服务名获取子节点
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        //服务地址
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            //本地缓存服务地址
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
        return result;
    }

    /**
     * 清空数据注册表
     *
     * @param zkClient zk客户端
     */
    public static void clearRegistry(CuratorFramework zkClient) {
        REGISTERED_PATH_SET.parallelStream().forEach(s -> {
            try {
                zkClient.delete().forPath(s);
            } catch (Exception e) {
                throw new RpcException(e.getMessage(), e.getCause());
            }
        });
        log.info("注册表清除成功:[{}]", REGISTERED_PATH_SET.toString());
    }


    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public static CuratorFramework getZkClient() {
        //检查用户是否设置zk地址
        Properties properties = PropertiesFileUtils.readPropertiesFile(RpcConstant.RPC_CONFIG_PATH);
        if (properties != null) {
            defaultZookeeperAddress = properties.getProperty(RpcConstant.ZK_ADDRESS);
        }
        //检测zkClient是否存在并启动如果是直接返回
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        //重试策略。重试3次 每次休眠1秒
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory
                .builder()
                .connectString(defaultZookeeperAddress)
                .retryPolicy(retryPolicy).build();
        zkClient.start();
        return zkClient;
    }

    /**
     * 注册监听指定节点子目录的增删改操作
     *
     * @param rpcServiceName 服务名
     * @param zkClient       zk客户端
     *                       所有机器约定在父目录下创建临时目录节点，然后监听父目录节点的子节点变化消息。
     *                       一旦有机器挂掉，该机器与 zookeeper的连接断开，其所创建的临时目录节点被删除，
     *                       所有其他机器都收到通知：某个兄弟目录被删除，于是，所有人都知道：它上船了
     */
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        //对指定路径节点的一级子目录监听，不对该节点的操作监听，对其子目录的增删改操作监听
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddress);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }
}
