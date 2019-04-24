package mmp.filter.impl;

import mmp.filter.Filter;
import mmp.filter.FilterChain;
import mmp.flow.Flow;
import mmp.flow.FlowManager;
import mmp.flow.SemaphoreManager;
import mmp.model.RPCRequest;
import mmp.model.RPCResponse;

/*
 * 实例应该可以共享，类似Netty@Sharable
 * */
public class FlowFilter<I, O> implements Filter<I, O> {

    private static final int RETRY_COUNT = 1;

    @Override
    public void filter(I input, O output, FilterChain<I, O> chain) throws Exception {

        RPCRequest rpcRequest = (RPCRequest) input;

        RPCResponse rpcResponse = (RPCResponse) output;
        int count = 0;

        boolean retry = true;
        String key = rpcRequest.getKey();

        Flow flow = FlowManager.getInstance().getFlow(key);

        while (retry) {
            if (SemaphoreManager.getInstance().tryAcquire(key)) {
                try {
                    chain.doFilter(input, output);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    SemaphoreManager.getInstance().release(key);
                    flow.signal();
                }
            } else {
                // 重试
                if (++count <= RETRY_COUNT) {

                    flow.awaitTime();
                } else {
                    retry = false;
                    throw new RuntimeException("flow limit...");
                }

            }
        }


    }
}
