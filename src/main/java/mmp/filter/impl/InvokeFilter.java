package mmp.filter.impl;

import mmp.RPCProvider;
import mmp.model.RPCRequest;
import mmp.model.RPCResponse;
import mmp.filter.Filter;
import mmp.filter.FilterChain;

import java.lang.reflect.Method;

public class InvokeFilter<I, O> implements Filter<I, O> {

    @Override
    public void filter(I input, O output, FilterChain<I, O> chain) throws Exception {

        // RPCFilterChain rpcFilterChain = (RPCFilterChain) chain;
        // Object proxy = rpcFilterChain.getProxy();
        // Method method = rpcFilterChain.getMethod();
        // Object[] args = rpcFilterChain.getArgs();

        RPCRequest rpcRequest = (RPCRequest) input;
        RPCResponse rpcResponse = (RPCResponse) output;

        Method method = rpcRequest.getMethod();
        // 听说有优化，关闭安全检查
        method.setAccessible(true);

        String methodName = rpcRequest.getMethodName();

        Class clz = method.getDeclaringClass();

        String className = rpcRequest.getClassName();

        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();

        Object[] parameters = rpcRequest.getParameters();

        // Object object = RPCProvider.getInstance().getService(className, clz);
        Object object = RPCProvider.getService(className);

        ((RPCResponse) output).setResult(method.invoke(object, parameters));


        chain.doFilter(input, output);

    }
}
