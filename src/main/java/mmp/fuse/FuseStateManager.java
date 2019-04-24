package mmp.fuse;

import java.util.concurrent.ConcurrentHashMap;

public class FuseStateManager {

    private final ConcurrentHashMap<String, FuseState> fuseStateConcurrentHashMap = new ConcurrentHashMap<>();

    private static FuseStateManager defaultInstance = new FuseStateManager();

    public static FuseStateManager getInstance() {
        return defaultInstance;
    }

    public FuseState getFuseState(String key) {
        FuseState fuseState = fuseStateConcurrentHashMap.get(key);
        if (fuseState == null) {
            fuseState = new FuseState(key);
            FuseState old = fuseStateConcurrentHashMap.putIfAbsent(key, fuseState);
            if (old != null) {
                fuseState = old;
            }
        }
        return fuseState;
    }

}
