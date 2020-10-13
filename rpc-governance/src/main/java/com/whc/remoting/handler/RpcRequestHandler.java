package com.whc.remoting.handler;

import com.whc.exceptions.RpcException;
import com.whc.provider.ServiceProvider;
import com.whc.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author whc
 * @date 2020/10/11 20:54
 */
@Slf4j
@Component
public class RpcRequestHandler {
    @Autowired
    private ServiceProvider serviceProvider;


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
