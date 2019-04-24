package mmp.balance.impl;

import mmp.RPCClient;
import mmp.balance.ILoadBalance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class RoundStrategy implements ILoadBalance {

    private ConcurrentHashMap<String, Integer> routeCountEachJob = new ConcurrentHashMap<>();

    private long CACHE_VALID_TIME = 0;

    private int count(String serviceKey) {
        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            routeCountEachJob.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        }
        // count++
        Integer count = routeCountEachJob.get(serviceKey);
        count = (count == null || count > 1000000) ? (new Random().nextInt(100)) : ++count;
        routeCountEachJob.put(serviceKey, count);
        return count;
    }

    @Override
    public String route(String serviceKey, Map<String, List<RPCClient>> clientMap) {
        Set<String> addressSet = clientMap.keySet();
        // arr
        String[] addressArr = addressSet.toArray(new String[addressSet.size()]);
        // round
        return addressArr[count(serviceKey) % addressArr.length];
    }

}
