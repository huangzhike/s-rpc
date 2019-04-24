package mmp.filter.impl;

import mmp.fuse.FuseStateManager;
import mmp.model.RPCRequest;
import mmp.model.RPCResponse;
import mmp.filter.Filter;
import mmp.filter.FilterChain;
import mmp.window.SlideWindowsManager;

public class StatFilter<I, O> implements Filter<I, O> {

    private FuseStateManager circuitStateManager = FuseStateManager.getInstance();

    @Override
    public void filter(I input, O output, FilterChain<I, O> chain) throws Exception {

        RPCRequest rpcRequest = (RPCRequest) input;
        RPCResponse rpcResponse = (RPCResponse) output;
        String key = rpcRequest.getKey();
        SlideWindowsManager.getInstance().addTotalCount(key);
        try {
            chain.doFilter(input, output);
        } catch (Exception e) {
            SlideWindowsManager.getInstance().addErrorCount(key);
            throw new RuntimeException(e);
        }
    }
}
