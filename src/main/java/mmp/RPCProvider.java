package mmp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Data
@Accessors(chain = true)
public class RPCProvider {

    private static final Map<String, Object> instanceMap = new ConcurrentHashMap<>();
    private static final Map<String, Class> classMap = new ConcurrentHashMap<>();

    public static Object addService(String interfaceName, Object service) {

        return instanceMap.put(interfaceName, service);

    }

    public static void removeService(String interfaceName) {
        instanceMap.remove(interfaceName);
        classMap.remove(interfaceName);
    }


    @SuppressWarnings("unchecked")
    public static <T> T getService(String interfaceName, T interfaceClass) {

        Object service = instanceMap.get(interfaceName);
        return (T) service;

    }

    public static Object getService(String interfaceName) throws Exception {

        Object service = instanceMap.get(interfaceName);
        if (service == null) {
            Class i = classMap.get(interfaceName);
            if (i != null) service = i.newInstance();
            Object repeatedObject = instanceMap.putIfAbsent(interfaceName, service);
            if (repeatedObject != null) service = repeatedObject;
        }
        return service;

    }


    static {
        init();
    }


    private static void init() {

    }

}
