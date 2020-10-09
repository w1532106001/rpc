package loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @author whc
 * @date 2020/10/9
 * @description
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    protected String doSelect(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
