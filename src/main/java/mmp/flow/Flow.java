package mmp.flow;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Flow {

    private static final Integer MAX_WAIT_COUNT = 10;

    private AtomicInteger waitCount = new AtomicInteger(0);

    private Integer waitSec = 10;

    private ReentrantLock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();


    public void signal() {

        lock.lock();
        try {
            waitCount.decrementAndGet();
            condition.signal();
        } finally {
            lock.unlock();
        }


    }


    public void awaitTime() throws Exception {
        int count = waitCount.get();

        if (count >= MAX_WAIT_COUNT) {
            throw new RuntimeException("flow max wait count exceeds...");
        }

        lock.lock();

        try {
            waitCount.incrementAndGet();
            condition.await(waitSec, TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }


    }


}
