package mmp.flow;

import java.util.concurrent.ConcurrentHashMap;

public class FlowManager {

    private ConcurrentHashMap<String, Flow> flowConcurrentHashMap = new ConcurrentHashMap<>();

    private static FlowManager defaultInstance = new FlowManager();

    public static FlowManager getInstance() {
        return defaultInstance;
    }


    public Flow getFlow(String key) {
        Flow flow = flowConcurrentHashMap.get(key);
        if (flow == null) {
            flow = new Flow();
            Flow old = flowConcurrentHashMap.putIfAbsent(key, flow);
            if (old != null) {
                flow = old;
            }
        }
        return flow;
    }

    public void removeFlow(String key) {
        flowConcurrentHashMap.remove(key);
    }


}
