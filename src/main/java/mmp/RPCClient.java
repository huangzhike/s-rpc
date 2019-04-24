package mmp;

import mmp.model.RPCRequest;

public interface RPCClient {

    Long generateRequestId();

    void send(RPCRequest rpcRequest);

}
