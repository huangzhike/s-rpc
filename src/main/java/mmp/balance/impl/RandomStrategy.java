package mmp.balance.impl;

import mmp.RPCClient;
import mmp.balance.ILoadBalance;

import java.util.*;


public class RandomStrategy implements ILoadBalance {

    private Random random = new Random();

    @Override
    public String route(String serviceKey, Map<String, List<RPCClient>> clientMap) {
        Set<String> addressSet = clientMap.keySet();
        // arr
        String[] addressArr = addressSet.toArray(new String[addressSet.size()]);
        // random
        return addressArr[random.nextInt(addressSet.size())];
    }

}
