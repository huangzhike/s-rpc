package mmp.balance;

import mmp.RPCClient;

import java.util.List;
import java.util.Map;

public interface ILoadBalance {

    String route(String serviceKey, Map<String, List<RPCClient>> clientMap);

}
