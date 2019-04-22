package mmp.balance.impl;

import mmp.RPCClient;
import mmp.balance.ILoadBalance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Least Recently Used
 * */
public class LRUStrategy implements ILoadBalance {

    private ConcurrentHashMap<String, LinkedHashMap<String, String>> jobLRUMap = new ConcurrentHashMap<>();

    private long CACHE_VALID_TIME = 0;

    public String doRoute(String serviceKey, Set<String> addressSet) {

        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            jobLRUMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        }

        // init lru
        LinkedHashMap<String, String> lruItem = jobLRUMap.get(serviceKey);
        if (lruItem == null) {
            /**
             * LinkedHashMap
             * a、accessOrder：true=访问顺序排序（get/put时排序）/ACCESS-LAST；false=插入顺序排期/FIFO；
             * b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
             */
            lruItem = new LinkedHashMap<String, String>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return super.size() > 1000;
                }
            };
            jobLRUMap.putIfAbsent(serviceKey, lruItem);
        }

        // put
        for (String address : addressSet) {
            if (!lruItem.containsKey(address)) lruItem.put(address, address);
        }

        // load
        String eldestKey = lruItem.entrySet().iterator().next().getKey();
        String eldestValue = lruItem.get(eldestKey);
        return eldestValue;
    }

    @Override
    public String route(String serviceKey, Map<String, List<RPCClient>> clientMap) {
        Set<String> addressSet = clientMap.keySet();
        return doRoute(serviceKey, addressSet);
    }

}
