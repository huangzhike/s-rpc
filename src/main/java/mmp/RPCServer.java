package mmp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RPCServer {

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private final Map<String, Class> interfaceMap = new ConcurrentHashMap<>();

    public RPCServer() {
        init();
    }

    private void init() {

    }


}
