package com.whc.loadbalance;

import java.util.List;

/**
 * @author whc
 * @date 2020/10/9
 * @description
 */

public interface LoadBalance {
    /**
     * 通过负载均衡算法选择服务地址
     *
     * @param serviceAddresses 地址列表
     * @return 服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses);
}
