package mmp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Data
@Accessors(chain = true)
public class RPCFuture implements Future {

    private RPCResponse rpcResponse;

    private String requestId;

    private int timeOut = RPCConfig.DEFAULT_TIMEOUT;

    private volatile boolean done = false;

    private volatile boolean cancelled = false;

    private List<RPCCallback> rpcCallbackList = new ArrayList<>();

    private Semaphore semaphore = new Semaphore(0);

    public RPCFuture() {

    }

    public RPCFuture(int timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public RPCResponse get() throws RuntimeException {
        return get(timeOut, TimeUnit.SECONDS);
    }

    @Override
    public RPCResponse get(long timeout, TimeUnit unit) throws RuntimeException {

        try {
            if (!semaphore.tryAcquire(timeout, unit)) {
                throw new RuntimeException("time out...");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return rpcResponse;
    }


    public void success(RPCResponse rpcResponse) {
        setRpcResponse(rpcResponse);
        done = true;

        if (rpcCallbackList != null && rpcCallbackList.size() > 0) {
            RPCContext.getCallbackExecutorService().submit(() -> rpcCallbackList.forEach(rpcCallback -> rpcCallback.onSuccess()));

        }

        semaphore.release();
    }

    public void fail(RPCResponse rpcResponse) {
        setRpcResponse(rpcResponse);
        done = true;

        if (rpcCallbackList != null && rpcCallbackList.size() > 0) {
            RPCContext.getCallbackExecutorService().submit(() -> rpcCallbackList.forEach(rpcCallback -> rpcCallback.onFailure()));
        }
        semaphore.release();
    }
}
