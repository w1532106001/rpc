
import org.junit.jupiter.api.Test;
import registry.ZKService;
import registry.zookeeper.ZKServiceImpl;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author shuang.kou
 * @createTime 2020年05月31日 16:25:00
 */
class ZkServiceTest {

    @Test
    void should_register_service_successful_and_lookup_service_by_service_name() {
        ZKService zkService = new ZKServiceImpl();
        InetSocketAddress givenInetSocketAddress = new InetSocketAddress("127.0.0.1", 9333);
        zkService.registerService("hello-service", givenInetSocketAddress);
        InetSocketAddress acquiredInetSocketAddress = zkService.findService("hello-service");
        assertEquals(givenInetSocketAddress.toString(), acquiredInetSocketAddress.toString());
    }
}
