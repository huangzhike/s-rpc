package mmp;

import mmp.balance.ILoadBalance;
import mmp.model.RPCRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class RPCConsumer {


    private Map<String, List<RPCClient>> clientMap = new ConcurrentHashMap<>();

    private List<RPCClient> clientList;


    public <T> T build(Class<T> service) {
        return build(service, new RequestConfig());
    }


    public <T> T build(Class<T> service, RequestConfig requestConfig) {
        return ProxyFactory.getProxy(service, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

                RPCRequest rpcRequest = new RPCRequest();

                rpcRequest.setClassName(method.getDeclaringClass().getName());
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setParameterTypes(method.getParameterTypes());
                rpcRequest.setParameters(args);

                ILoadBalance loadBalance = requestConfig.getLoadBalance();

                String serviceKey = rpcRequest.getClassName() + rpcRequest.getMethodName();

                RPCClient rpcClient = select(loadBalance, serviceKey, clientMap);

                rpcRequest.setRequestId(String.valueOf(rpcClient.generateRequestId()));

                RPCContext rpcContext = RPCContext.getOrCreateContext();

                rpcContext.setRpcRequest(rpcRequest);

                RPCFuture rpcFuture = new RPCFuture();
                rpcFuture.setRequestId(rpcRequest.getRequestId());

                if (CallType.CALLBACK == rpcContext.getCallType()) {
                    List<RPCCallback> callbackList = rpcContext.getRpcCallbackList();
                    rpcFuture.setRpcCallbackList(callbackList);
                }

                rpcContext.setRpcFuture(rpcFuture);

                RPCContext.addPendingFuture(rpcFuture);

                RPCContext.setContext(rpcContext);


                rpcRequest = beforeCall(rpcRequest);

                rpcClient.send(rpcRequest);

                afterCall();

                CallType callType = rpcContext.getCallType();

                if (CallType.CALLBACK != callType && CallType.FUTURE != callType && CallType.ONEWAY != callType) {
                    try {
                        return rpcFuture.get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        RPCContext.removeContext();
                    }
                }
                return null;
            }
        });
    }


    protected RPCRequest beforeCall(RPCRequest rpcRequest) {

        return rpcRequest;
    }

    protected void afterCall() {

    }


    public RPCFuture futureCall(Runnable runnable) {

        RPCContext rpcContext = RPCContext.getOrCreateContext();
        rpcContext.setCallType(CallType.FUTURE);
        RPCContext.setContext(rpcContext);

        runnable.run();
        RPCFuture rpcFuture = RPCContext.getContext().getRpcFuture();

        RPCContext.removeContext();
        return rpcFuture;
    }


    public RPCFuture callbackCall(Runnable runnable, List<RPCCallback> callbackList) {
        RPCContext rpcContext = RPCContext.getOrCreateContext();
        rpcContext.setCallType(CallType.CALLBACK);
        rpcContext.setRpcCallbackList(callbackList);
        RPCContext.setContext(rpcContext);
        runnable.run();
        RPCFuture rpcFuture = RPCContext.getContext().getRpcFuture();
        RPCContext.removeContext();
        return rpcFuture;
    }

    public void onewayCall(Runnable runnable) {
        RPCContext rpcContext = RPCContext.getOrCreateContext();
        rpcContext.setCallType(CallType.ONEWAY);
        RPCContext.setContext(rpcContext);
        runnable.run();
        RPCContext.removeContext();
    }

    private RPCClient select(ILoadBalance loadBalance, String serviceKey, Map<String, List<RPCClient>> clientMap) {
        Random random = new Random();
        List<RPCClient> clientList = clientMap.get(loadBalance.route(serviceKey, clientMap));
        int size = clientList.size();
        return clientList.get(random.nextInt(size));
    }

    public void start() {

        clientList.forEach(rpcClient -> {

        });

    }


}
