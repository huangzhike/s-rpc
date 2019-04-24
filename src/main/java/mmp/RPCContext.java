package mmp;

import lombok.Data;
import lombok.experimental.Accessors;
import mmp.model.RPCRequest;
import mmp.model.RPCResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Data
@Accessors(chain = true)
public class RPCContext<T> {

    private static final ThreadLocal<RPCContext> rpcContextThreadLocal = new ThreadLocal<>();


    private static final Map<String, RPCFuture> pendingFuturePool = new ConcurrentHashMap<>();

    private static final ExecutorService callbackExecutorService = Executors.newSingleThreadExecutor();

    private RPCRequest rpcRequest;
    private CallType callType;

    private List<RPCCallback> rpcCallbackList;
    private RPCFuture rpcFuture;


    public static Map<String, RPCFuture> getPendingFuturePool() {
        return pendingFuturePool;
    }

    public static void addPendingFuture(RPCFuture rpcFuture) {
        pendingFuturePool.put(rpcFuture.getRequestId(), rpcFuture);
    }

    public static void resolveFuture(String requestId, RPCResponse rpcResponse) {
        RPCFuture rpcFuture = pendingFuturePool.get(requestId);
        if (rpcFuture != null && rpcResponse != null) {
            if (rpcResponse.getError() != null) {
                rpcFuture.fail(rpcResponse);
            } else {
                rpcFuture.success(rpcResponse);
            }
        }
    }

    public static ExecutorService getCallbackExecutorService() {
        return callbackExecutorService;
    }


    public static void setContext(RPCContext rpcContext) {
        rpcContextThreadLocal.set(rpcContext);
    }

    public static RPCContext getOrCreateContext() {
        RPCContext rpcContext = rpcContextThreadLocal.get();
        if (rpcContext == null) {
            rpcContext = new RPCContext();
            setContext(rpcContext);
        }
        return rpcContext;
    }

    public static RPCContext getContext() {

        return rpcContextThreadLocal.get();
    }

    public static void removeContext() {
        rpcContextThreadLocal.remove();
    }


}
