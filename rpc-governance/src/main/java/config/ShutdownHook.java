package config;

import util.CuratorUtils;

/**
 * @author whc
 * @date 2020/10/11 20:12
 */
public class ShutdownHook {
    private static final ShutdownHook SHUTDOWN_HOOK = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return SHUTDOWN_HOOK;
    }

    /**
     * 当jvm停止时 清除所有已注册服务
     */
    public void clearAllRegistryService(){
        Runtime.getRuntime().addShutdownHook(new Thread(()-> CuratorUtils.clearRegistry(CuratorUtils.getZkClient())));
    }
}
