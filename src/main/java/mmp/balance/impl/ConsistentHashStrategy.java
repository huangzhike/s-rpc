package mmp.balance.impl;

import mmp.RPCClient;
import mmp.balance.ILoadBalance;

import java.security.MessageDigest;
import java.util.*;

/**
 * Consistent Hashing
 */
public class ConsistentHashStrategy implements ILoadBalance {

    private final int VIRTUAL_NODE_NUM = 5;

    /**
     * get hash code on 2^32 ring
     */
    private long hash(String key) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes;
        try {
            keyBytes = key.getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Unknown string :" + key, e);
        }
        md5.update(keyBytes);
        byte[] digest = md5.digest();
        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24) | ((long) (digest[2] & 0xFF) << 16) | ((long) (digest[1] & 0xFF) << 8) | (digest[0] & 0xFF);
        long truncateHashCode = hashCode & 0xffffffffL;
        return truncateHashCode;
    }

    public String doRoute(String serviceKey, Set<String> addressSet) {

        // ------A1------A2-------A3------
        // -----------J1------------------
        TreeMap<Long, String> addressRing = new TreeMap<>();
        for (String address : addressSet) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                long addressHash = hash("SHARD-" + address + "-NODE-" + i);
                addressRing.put(addressHash, address);
            }
        }

        long jobHash = hash(serviceKey);
        SortedMap<Long, String> lastRing = addressRing.tailMap(jobHash);
        return !lastRing.isEmpty() ? lastRing.get(lastRing.firstKey()) : addressRing.firstEntry().getValue();
    }

    @Override
    public String route(String serviceKey, Map<String, List<RPCClient>> clientMap) {
        Set<String> addressSet = clientMap.keySet();
        return doRoute(serviceKey, addressSet);
    }

}
