package loadbalance;

import java.util.List;

/**
 * @author whc
 * @date 2020/10/9
 * @description 抽象负载均衡算法
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    public String selectServiceAddress(List<String> serviceAddresses) {
        if (serviceAddresses == null && serviceAddresses.size() == 0) {
            return null;
        }
        if(serviceAddresses.size()==1){
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses);
    }

    protected abstract String doSelect(List<String> serviceAddresses);
}
