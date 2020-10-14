package com.whc.provider;

import com.whc.entity.RpcServiceProperties;

/**
 * @author whc
 * @date 2020/10/11 19:31
 * 服务提供
 */
public interface ServiceProvider {
    /**
     * 添加服务信息
     *
     * @param service              服务类
     * @param serviceClass         由服务实例对象实现的接口类
     * @param rpcServiceProperties 服务相关信息
     */
    void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties);

    /**
     * 获取服务类
     *
     * @param rpcServiceProperties 服务相关信息
     * @return 服务类
     */
    Object getService(RpcServiceProperties rpcServiceProperties);

    /**
     * 推送服务信息
     *
     * @param service              服务类
     * @param rpcServiceProperties 服务相关信息
     */
    void publishService(Object service, RpcServiceProperties rpcServiceProperties);

    /**
     * 推送服务信息
     *
     * @param service 服务类
     */
    void publishService(Object service);
}
