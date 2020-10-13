package com.whc.loadbalance;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * @author whc
 * @date 2020/10/9
 * @description
 */
@Component
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
