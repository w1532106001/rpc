package remoting.handler;

import exceptions.RpcException;
import lombok.extern.slf4j.Slf4j;
import provider.ServiceProvider;
import provider.ServiceProviderImpl;
import remoting.dto.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author whc
 * @date 2020/10/11 20:54
 */
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = new ServiceProviderImpl();
    }

    /**
     * 处理rpcRequest：调用相应的方法，然后返回该方法
     *
     * @param rpcRequest 请求信息
     * @return 相应的方法返回
     */
    public Object handler(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.toRpcProperties());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 执行接口方法并获取结果
     * @param rpcRequest 请求信息
     * @param service 服务类
     * @return 目标方法执行的结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            //获取执行方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            result = method.invoke(service,rpcRequest.getParameters());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(e.getMessage(),e);
        }
        return result;
    }

}
