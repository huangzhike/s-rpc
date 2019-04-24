package mmp.filter.impl;

import mmp.filter.Filter;
import mmp.filter.FilterChain;
import mmp.fuse.Fuse;
import mmp.fuse.FuseStateManager;
import mmp.fuse.FuseState;
import mmp.model.RPCRequest;
import mmp.model.RPCResponse;

public class FuseFilter<I, O> implements Filter<I, O> {

    @Override
    public void filter(I input, O output, FilterChain<I, O> chain) throws Exception {

        RPCRequest rpcRequest = (RPCRequest) input;

        RPCResponse rpcResponse = (RPCResponse) output;

        String key = rpcRequest.getKey();

        FuseState fuseState = FuseStateManager.getInstance().getFuseState(key);

        Fuse fuse = Fuse.getInstance();

        // CLOSE or HALF_OPEN
        if (fuse.tryPass(fuseState)) {
            try {
                chain.doFilter(input, output);
                // 调用成功，关闭HALF_OPEN
                fuse.success(fuseState);
            } catch (Exception e) {
                fuse.fail(fuseState);
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException();

    }
}
