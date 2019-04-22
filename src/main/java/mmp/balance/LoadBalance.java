package mmp.balance;


import mmp.balance.impl.*;

public enum LoadBalance {

    RANDOM(new RandomStrategy()),
    ROUND(new RoundStrategy()),
    LRU(new LRUStrategy()),
    LFU(new LFUStrategy()),
    CONSISTENT_HASH(new ConsistentHashStrategy());


    public final ILoadBalance loadBalance;

    LoadBalance(ILoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

}