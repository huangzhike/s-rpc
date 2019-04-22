package mmp;

public interface RPCClient {

    Long generateRequestId();

    void send(RPCRequest rpcRequest);

}
